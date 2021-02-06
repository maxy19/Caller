package com.maxy.caller.admin.worker;

import com.maxy.caller.admin.cache.CacheService;
import com.maxy.caller.admin.service.AdminWorker;
import com.maxy.caller.bo.TaskDetailInfoBO;
import com.maxy.caller.common.utils.DateUtils;
import com.maxy.caller.common.utils.JSONUtils;
import com.maxy.caller.core.config.ThreadPoolConfig;
import com.maxy.caller.core.config.ThreadPoolRegisterCenter;
import com.maxy.caller.core.service.TaskBaseInfoService;
import com.maxy.caller.core.service.TaskDetailInfoService;
import com.maxy.caller.core.service.TaskLockService;
import com.maxy.caller.core.service.TaskLogService;
import com.maxy.caller.pojo.DictionaryIndexData;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.maxy.caller.core.enums.ExecutionStatusEnum.ONLINE;
import static com.maxy.caller.core.enums.ExecutionStatusEnum.READY;
import static com.maxy.caller.core.enums.GenerateKeyEnum.DICTIONARY_INDEX;
import static com.maxy.caller.core.enums.GenerateKeyEnum.INDEX_DATA_FORMAT;
import static com.maxy.caller.core.enums.GenerateKeyEnum.ZSET_FORMAT;
import static com.maxy.caller.core.utils.CallerUtils.filter;

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
    private TaskBaseInfoService taskBaseInfoService;
    @Resource
    private TaskLockService taskLockService;
    @Resource(name = "asTransactionManager")
    private DataSourceTransactionManager dataSourceTransactionManager;
    @Resource
    private TransactionDefinition transactionDefinition;

    private ExecutorService scheduleWorker = ThreadPoolConfig.getInstance().getSingleThreadExecutor(true);


    private volatile boolean toggle = true;

    @Override
    public void start() {
        scheduleWorker.execute(() -> {
            while (toggle) {
                try {
                    //入队
                    push();
                    //(打散时间防止极端情况都抢锁)
                    TimeUnit.DAYS.sleep(1);
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
        try {
            //开启事务
            transaction = dataSourceTransactionManager.getTransaction(transactionDefinition);
            //悲观锁
            taskLockService.lockForUpdate();
            //查询将要执行数据
            Date currentTime = new Date();
            Date date = DateUtils.addDays(1);
            preReadInfoList = taskDetailInfoService.getPreReadInfo(ONLINE.getCode(), currentTime, DateUtils.addDays(1));
            //校验
            if (CollectionUtils.isEmpty(preReadInfoList)) {
                log.warn("push#预读:{}-{}.没有找到对应信息!!", currentTime, date);
                return;
            }
            addCacheQueue(preReadInfoList);
            //更改状态为执行中
            taskDetailInfoService.updateStatusByIds(preReadInfoList.stream().map(TaskDetailInfoBO::getId).collect(Collectors.toList()), ONLINE.getCode(), READY.getCode());
            //释放DB锁
            dataSourceTransactionManager.commit(transaction);
        } catch (Exception e) {
            log.error("addZSetQueue#执行加入队列出现异常!!", e);
            if (Objects.nonNull(transaction)) {
                dataSourceTransactionManager.rollback(transaction);
            }
        }
        //记录log
        taskLogService.batchInsert(preReadInfoList);
    }

    private void addCacheQueue(List<TaskDetailInfoBO> preReadInfoList) {
        Map<String, JedisPool> nodeMap = cacheService.getNodeMap();
        log.info("addCacheQueue#nodeMap:{}", nodeMap);
        List<DictionaryIndexData> filter = filter(preReadInfoList);
        filter.forEach(indexData -> {
            long mapping = indexData.getTime() & nodeMap.size() / 2;
            //key:value=group+biz+topic:time:{0}
            cacheService.lpush(DICTIONARY_INDEX.getKey(), INDEX_DATA_FORMAT.join(indexData.getKey(), mapping, indexData.getTime()));
        });

        preReadInfoList.forEach(taskDetailInfoBO -> {
            String uniqueId = getUniqueName(taskDetailInfoBO);
            long time = taskDetailInfoBO.getExecutionTime().getTime();
            //消除bigKey则将队列通过{node-1}分片 打散映射
            long mapping = time & nodeMap.size() / 2;
            cacheService.zadd(ZSET_FORMAT.join(uniqueId, mapping), (double) time, JSONUtils.toJSONString(taskDetailInfoBO));
        });
    }


}
