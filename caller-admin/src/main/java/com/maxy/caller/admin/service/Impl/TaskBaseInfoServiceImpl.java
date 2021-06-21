package com.maxy.caller.admin.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.maxy.caller.bo.QueryConditionBO;
import com.maxy.caller.bo.TaskBaseInfoBO;
import com.maxy.caller.common.utils.BeanCopyUtils;
import com.maxy.caller.common.utils.DateUtils;
import com.maxy.caller.core.exception.BusinessException;
import com.maxy.caller.core.service.Cache;
import com.maxy.caller.core.service.TaskBaseInfoService;
import com.maxy.caller.model.TaskBaseInfo;
import com.maxy.caller.model.TaskGroup;
import com.maxy.caller.persistent.example.TaskBaseInfoExample;
import com.maxy.caller.persistent.example.TaskGroupExample;
import com.maxy.caller.persistent.mapper.TaskBaseInfoMapper;
import com.maxy.caller.persistent.mapper.TaskGroupMapper;
import com.maxy.caller.pojo.Pagination;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.maxy.caller.core.enums.ExceptionEnum.FOUND_NOT_CURRENT_BASE_INFO;

/**
 * @Author maxy
 **/
@Service
public class TaskBaseInfoServiceImpl implements TaskBaseInfoService {
    @Resource
    private TaskBaseInfoMapper taskBaseInfoMapper;
    @Resource
    private TaskGroupMapper taskGroupMapper;
    @Resource
    private Cache cache;

    @Override
    public PageInfo<TaskBaseInfoBO> list(QueryConditionBO queryConditionBO) {
        Pagination pagination = queryConditionBO.getPagination();
        PageHelper.startPage(pagination.getPageNum(), pagination.getPageSize());
        TaskGroupExample example = new TaskGroupExample();
        example.createCriteria().andGroupKeyEqualTo(queryConditionBO.getGroupKey())
                .andBizKeyEqualTo(queryConditionBO.getBizKey());
        List<TaskGroup> taskGroups = taskGroupMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(taskGroups)) {
            TaskGroup taskGroup = taskGroups.get(0);
            TaskBaseInfoExample taskBaseInfoExample = new TaskBaseInfoExample();
            taskBaseInfoExample.createCriteria().andIdEqualTo(taskGroup.getId());
            List<TaskBaseInfo> taskBaseInfos = taskBaseInfoMapper.selectByExample(taskBaseInfoExample);
            return new PageInfo(BeanCopyUtils.copyListProperties(taskBaseInfos, TaskBaseInfoBO::new));
        }
        return new PageInfo<>();
    }

    @Override
    public Boolean save(TaskBaseInfoBO taskBaseInfoBO) {
        TaskBaseInfo taskDetailInfo = new TaskBaseInfo();
        BeanCopyUtils.copy(taskBaseInfoBO, taskDetailInfo);
        taskDetailInfo.setCreateTime(DateUtils.getNowTime());
        taskDetailInfo.setUpdateTime(DateUtils.getNowTime());
        if (taskBaseInfoMapper.insertSelective(taskDetailInfo) > 0) {
            return cacheInfo(taskBaseInfoBO);
        }
        return false;
    }

    private boolean cacheInfo(TaskBaseInfoBO taskBaseInfoBO) {
        Map<String, String> map = new HashMap<>();
        map.put(ALARM_EMAIL, taskBaseInfoBO.getAlarmEmail());
        map.put(STRATEGY_VALUE, taskBaseInfoBO.getExecutorRouterStrategy().toString());
        cache.hmset(getUniqueName(taskBaseInfoBO), map, ONE_HOUR);
        return true;
    }

    @Override
    public Boolean update(TaskBaseInfoBO taskBaseInfoBO) {
        TaskBaseInfo taskBaseInfo = new TaskBaseInfo();
        BeanCopyUtils.copy(taskBaseInfoBO, taskBaseInfo);
        TaskBaseInfoExample taskBaseInfoExample = new TaskBaseInfoExample();
        TaskBaseInfoExample.Criteria criteria = taskBaseInfoExample.createCriteria();
        criteria.andGroupKeyEqualTo(taskBaseInfo.getGroupKey());
        criteria.andBizKeyEqualTo(taskBaseInfo.getBizKey());
        criteria.andTopicEqualTo(taskBaseInfo.getTopic());
        taskBaseInfo.setUpdateTime(DateUtils.getNowTime());
        if (taskBaseInfoMapper.updateByExampleSelective(taskBaseInfo, taskBaseInfoExample) > 0) {
            cacheInfo(taskBaseInfoBO);
        }
        return false;
    }

    @Override
    public Boolean delete(Long taskInfoId) {
        return taskBaseInfoMapper.deleteByPrimaryKey(taskInfoId) > 0;
    }

    @Override
    public TaskBaseInfoBO getById(Long taskBaseInfoId) {
        TaskBaseInfo taskBaseInfo = taskBaseInfoMapper.selectByPrimaryKey(taskBaseInfoId);
        TaskBaseInfoBO taskBaseInfoBO = new TaskBaseInfoBO();
        BeanCopyUtils.copy(taskBaseInfo, taskBaseInfoBO);
        return taskBaseInfoBO;
    }

    @Override
    public TaskBaseInfoBO getByUniqueKey(String groupKey, String bizKey, String topic) {
        TaskBaseInfoExample example = new TaskBaseInfoExample();
        TaskBaseInfoExample.Criteria criteria = example.createCriteria();
        criteria.andGroupKeyEqualTo(groupKey);
        criteria.andBizKeyEqualTo(bizKey);
        criteria.andTopicEqualTo(topic);
        List<TaskBaseInfo> taskBaseInfos = taskBaseInfoMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(taskBaseInfos)) {
            throw new BusinessException(FOUND_NOT_CURRENT_BASE_INFO);
        }
        TaskBaseInfoBO taskBaseInfoBO = new TaskBaseInfoBO();
        BeanCopyUtils.copy(taskBaseInfos.get(0), taskBaseInfoBO);
        return taskBaseInfoBO;
    }

    @Override
    public List<TaskBaseInfoBO> getTaskInfoBOList(Date currentTime, Byte executionStatus, int addSecond) {
        TaskBaseInfoExample example = new TaskBaseInfoExample();
        List<TaskBaseInfo> taskBaseInfos = taskBaseInfoMapper.selectByExample(example);
        return BeanCopyUtils.copyListProperties(taskBaseInfos, TaskBaseInfoBO::new);
    }

    /**
     * 缓存路由策略
     *
     * @return
     */
    @Override
    public Byte getRouterStrategy(String groupKey, String bizKey, String topic) {
        String strategyValue = cache.hget(getUniqueName(groupKey, bizKey, topic), STRATEGY_VALUE);
        if (StringUtils.isBlank(strategyValue)) {
            TaskBaseInfoBO taskBaseInfoBO = getByUniqueKey(groupKey, bizKey, topic);
            cache.hmset(getUniqueName(taskBaseInfoBO), STRATEGY_VALUE, String.valueOf(taskBaseInfoBO.getExecutorRouterStrategy()), ONE_HOUR);
            return taskBaseInfoBO.getExecutorRouterStrategy();
        }
        return Byte.valueOf(strategyValue);
    }

    @Override
    public String getAlarmEmail(String groupKey, String bizKey, String topic) {
        String alarmEmail = cache.hget(getUniqueName(groupKey, bizKey, topic), ALARM_EMAIL);
        if (StringUtils.isBlank(alarmEmail)) {
            TaskBaseInfoBO taskBaseInfoBO = getByUniqueKey(groupKey, bizKey, topic);
            cache.hmset(getUniqueName(taskBaseInfoBO), ALARM_EMAIL, String.valueOf(taskBaseInfoBO.getExecutorRouterStrategy()), ONE_HOUR);
            return taskBaseInfoBO.getAlarmEmail();
        }
        return alarmEmail;
    }
}
