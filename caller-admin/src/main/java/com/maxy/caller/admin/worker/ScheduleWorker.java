package com.maxy.caller.admin.worker;

import com.maxy.caller.admin.cache.CacheService;
import com.maxy.caller.admin.service.AdminWorker;
import com.maxy.caller.bo.TaskDetailInfoBO;
import com.maxy.caller.common.utils.DateUtils;
import com.maxy.caller.common.utils.JSONUtils;
import com.maxy.caller.core.config.GeneralConfigCenter;
import com.maxy.caller.core.config.ThreadPoolConfig;
import com.maxy.caller.core.config.ThreadPoolRegisterCenter;
import com.maxy.caller.core.service.TaskDetailInfoService;
import com.maxy.caller.core.service.TaskLockService;
import com.maxy.caller.core.service.TaskLogService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.maxy.caller.core.constant.ThreadConstant.ADMIN_SCHEDULE_WORKER_THREAD_POOL;
import static com.maxy.caller.core.enums.ExecutionStatusEnum.ONLINE;
import static com.maxy.caller.core.enums.ExecutionStatusEnum.READY;
import static com.maxy.caller.core.enums.GenerateKeyEnum.DETAIL_TASK_INFO;
import static com.maxy.caller.core.enums.GenerateKeyEnum.ZSET_QUEUE_FORMAT;

/**
 * 扫描taskInfo信息放入队列
 *
 * @Author maxy
 **/
@Component
@Log4j2
public class ScheduleWorker implements AdminWorker {

    @Resource
    private CacheService cacheService;
    @Resource
    private TaskDetailInfoService taskDetailInfoService;
    @Resource
    private TaskLogService taskLogService;
    @Resource
    private TaskLockService taskLockService;
    @Resource(name = "asTransactionManager")
    private DataSourceTransactionManager dataSourceTransactionManager;
    @Resource
    private TransactionDefinition transactionDefinition;

    private ExecutorService scheduleWorker = ThreadPoolConfig.getInstance().getSingleThreadExecutor(true,ADMIN_SCHEDULE_WORKER_THREAD_POOL);
    @Resource
    private GeneralConfigCenter config;

    private volatile boolean toggle = true;

    @Override
    public void start() {
        scheduleWorker.execute(() -> {
            while (toggle) {
                try {
                    //入队
                    push();
                    //(打散时间防止极端情况都抢锁)
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (Exception e) {
                    log.error("加入队列定时出现异常！！", e);
                }
            }
        });
    }


    @Override
    public void stop() {
        toggle = false;
        ThreadPoolRegisterCenter.destroy(scheduleWorker);
    }

    /**
     * 查询并放入ZSet中
     */
    private void push() {
        TransactionStatus transaction = null;
        List<TaskDetailInfoBO> preReadInfoList = new ArrayList<>();
        boolean flag = true;
        try {
            //开启事务
            transaction = dataSourceTransactionManager.getTransaction(transactionDefinition);
            //悲观锁
            taskLockService.lockForUpdate();
            //查询将要执行数据
            Date endDate = DateUtils.addSecond(config.getPushCycleTime());
            preReadInfoList = taskDetailInfoService.getPreReadInfo(ONLINE.getCode(), endDate, config.getPreReadLimit());
            //校验
            if (CollectionUtils.isEmpty(preReadInfoList)) {
                dataSourceTransactionManager.commit(transaction);
                return;
            }
            log.info("push#预读:小于[{}]的所有任务,数据量为:{}", endDate, preReadInfoList.size());
            addZSetQueue(preReadInfoList, config.getTotalSlot());
            //更改状态为执行中
            taskDetailInfoService.updateStatusByIds(preReadInfoList.stream().map(TaskDetailInfoBO::getId).collect(Collectors.toList()), ONLINE.getCode(), READY.getCode());
            //释放DB锁
            dataSourceTransactionManager.commit(transaction);
        } catch (Exception e) {
            log.error("push#执行加入队列出现异常!!", e);
            flag = false;
            if (Objects.nonNull(transaction)) {
                dataSourceTransactionManager.rollback(transaction);
            }
        }
        //记录log
        if (flag) {
            taskLogService.batchInsert(preReadInfoList, READY.getCode());
        }
    }
    /**
     * 添加zset队列
     * @param preReadInfoList
     * @param size
     */
    private void addZSetQueue(List<TaskDetailInfoBO> preReadInfoList, int size) {
        preReadInfoList.forEach(taskDetailInfoBO -> {
            long time = taskDetailInfoBO.getExecutionTime().getTime();
            //消除bigKey则将队列通过{node-1}分片 打散映射
            long slot = mod(time, size);
            cacheService.zadd(ZSET_QUEUE_FORMAT.join(slot), (double) time, JSONUtils.toJSONString(taskDetailInfoBO));
            cacheDetailTaskInfo(taskDetailInfoBO, time);
        });
    }


    /**
     * 缓存详情任务信息
     * 过期时间 = 距离执行时间+执行时间(10000-60000毫秒随机值)避免相同时间同时过期
     *
     * @param taskDetailInfoBO
     * @param time
     */
    private void cacheDetailTaskInfo(TaskDetailInfoBO taskDetailInfoBO, long time) {
        long remainingTime = TimeUnit.MILLISECONDS.toSeconds(time - System.currentTimeMillis()) + TimeUnit.MILLISECONDS.toMillis(RandomUtils.nextInt(10000, 60000));
        //单个detail信息
        if (remainingTime - ONE_SECOND > 0) {
            cacheService.set(DETAIL_TASK_INFO.join(taskDetailInfoBO.getId()), (int) remainingTime, JSONUtils.toJSONString(taskDetailInfoBO));
        }
    }
}
