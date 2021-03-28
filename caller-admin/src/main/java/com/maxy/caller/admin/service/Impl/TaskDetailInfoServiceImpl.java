package com.maxy.caller.admin.service.Impl;

import com.github.pagehelper.PageInfo;
import com.maxy.caller.admin.cache.CacheService;
import com.maxy.caller.admin.config.AdminConfigCenter;
import com.maxy.caller.bo.QueryConditionBO;
import com.maxy.caller.bo.TaskDetailInfoBO;
import com.maxy.caller.common.utils.BeanCopyUtils;
import com.maxy.caller.common.utils.DateUtils;
import com.maxy.caller.common.utils.JSONUtils;
import com.maxy.caller.core.enums.ExecutionStatusEnum;
import com.maxy.caller.core.service.TaskDetailInfoService;
import com.maxy.caller.model.TaskDetailInfo;
import com.maxy.caller.model.TaskGroup;
import com.maxy.caller.persistent.example.TaskDetailInfoExample;
import com.maxy.caller.persistent.example.TaskGroupExample;
import com.maxy.caller.persistent.mapper.TaskDetailInfoExtendMapper;
import com.maxy.caller.persistent.mapper.TaskGroupMapper;
import com.maxy.caller.pojo.Pagination;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.maxy.caller.core.enums.GenerateKeyEnum.LIST_QUEUE_FORMAT_BACKUP;

/**
 * @Author maxy
 **/
@Service
public class TaskDetailInfoServiceImpl implements TaskDetailInfoService {
    @Resource
    private TaskDetailInfoExtendMapper taskDetailInfoExtendMapper;
    @Resource
    private TaskGroupMapper taskGroupMapper;
    @Resource
    private CacheService cacheService;
    @Resource
    private AdminConfigCenter config;

    @Override
    public PageInfo<TaskDetailInfoBO> list(QueryConditionBO queryConditionBO) {
        TaskGroupExample example = new TaskGroupExample();
        example.createCriteria().andGroupKeyEqualTo(queryConditionBO.getGroupKey())
                .andBizKeyEqualTo(queryConditionBO.getBizKey());
        List<TaskGroup> taskGroups = taskGroupMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(taskGroups)) {
            TaskGroup taskGroup = taskGroups.stream().filter(Objects::nonNull).findFirst().get();
            TaskDetailInfoExample taskDetailInfoExample = new TaskDetailInfoExample();
            taskDetailInfoExample.createCriteria().andIdEqualTo(taskGroup.getId());
            List<TaskDetailInfo> taskDetailInfos = taskDetailInfoExtendMapper.selectByExample(taskDetailInfoExample);
            Pagination pagination = queryConditionBO.getPagination();
            taskDetailInfoExample.setOrderByClause(" limit " + pagination.getPageNum() + " , " + pagination.getPageSize());
            return new PageInfo(BeanCopyUtils.copyListProperties(taskDetailInfos, TaskDetailInfoBO::new));
        }
        return new PageInfo<>();
    }

    @Override
    public Boolean save(TaskDetailInfoBO taskDetailInfoBO) {
        TaskDetailInfo taskDetailInfo = new TaskDetailInfo();
        BeanCopyUtils.copy(taskDetailInfoBO, taskDetailInfo);
        taskDetailInfo.setCreateTime(DateUtils.getNowTime());
        taskDetailInfo.setUpdateTime(DateUtils.getNowTime());
        return taskDetailInfoExtendMapper.insertSelective(taskDetailInfo) > 0;
    }

    @Override
    public Boolean update(TaskDetailInfoBO taskDetailInfoBO) {
        TaskDetailInfo taskDetailInfo = new TaskDetailInfo();
        BeanCopyUtils.copy(taskDetailInfoBO, taskDetailInfo);
        TaskDetailInfoExample taskDetailInfoExample = new TaskDetailInfoExample();
        TaskDetailInfoExample.Criteria criteria = taskDetailInfoExample.createCriteria();
        criteria.andGroupKeyEqualTo(taskDetailInfoBO.getGroupKey());
        criteria.andBizKeyEqualTo(taskDetailInfoBO.getGroupKey());
        criteria.andTopicEqualTo(taskDetailInfoBO.getTopic());
        taskDetailInfo.setUpdateTime(DateUtils.getNowTime());
        return taskDetailInfoExtendMapper.updateByPrimaryKeySelective(taskDetailInfo) > 0;
    }

    @Override
    public Boolean updateStatus(Long id, Byte status) {
        TaskDetailInfoBO taskDetailInfoBO = getByInfoId(id);
        if(!ExecutionStatusEnum.isFinalState(taskDetailInfoBO.getExecutionStatus())){
            taskDetailInfoBO.setExecutionStatus(status);
            return false;
        }
        TaskDetailInfo taskDetailInfo = new TaskDetailInfo();
        BeanCopyUtils.copy(taskDetailInfoBO, taskDetailInfo);
        return taskDetailInfoExtendMapper.updateByPrimaryKeySelective(taskDetailInfo) > 0;
    }

    @Override
    public Boolean delete(Long taskDetailInfoId) {
        return taskDetailInfoExtendMapper.deleteByPrimaryKey(taskDetailInfoId) > 0;
    }

    @Override
    public TaskDetailInfoBO getByInfoId(Long taskInfoId) {
        TaskDetailInfo taskDetailInfo = taskDetailInfoExtendMapper.selectByPrimaryKey(taskInfoId);
        TaskDetailInfoBO taskInfoItemBO = new TaskDetailInfoBO();
        BeanCopyUtils.copy(taskDetailInfo, taskInfoItemBO);
        return taskInfoItemBO;
    }

    @Override
    public List<TaskDetailInfoBO> getPreReadInfo(Byte status, Date endTime, String limitValue) {
        TaskDetailInfoExample example = new TaskDetailInfoExample();
        example.createCriteria().andExecutionStatusEqualTo(status)
                .andExecutionTimeLessThanOrEqualTo(endTime);
        example.setOrderByClause("id limit 0," + limitValue);
        List<TaskDetailInfo> taskDetailInfos = taskDetailInfoExtendMapper.selectByExample(example);
        return BeanCopyUtils.copyListProperties(taskDetailInfos, TaskDetailInfoBO::new);
    }

    @Override
    public boolean batchInsert(List<TaskDetailInfoBO> taskDetailInfoBOList) {
        return taskDetailInfoExtendMapper.batchInsert(BeanCopyUtils.copyListProperties(taskDetailInfoBOList, TaskDetailInfo::new)) > 0;
    }

    @Override
    public boolean updateStatusByIds(List<Long> ids, Byte sourceStatus, Byte targetStatus) {
        return taskDetailInfoExtendMapper.updateStatusByIds(ids, targetStatus, sourceStatus) > 0;
    }

    @Override
    public List<TaskDetailInfoBO> getTaskDetailList(String groupKey, String bizKey, String topic) {
        TaskDetailInfoExample example = new TaskDetailInfoExample();
        example.createCriteria().andGroupKeyEqualTo(groupKey).andBizKeyEqualTo(bizKey).andTopicEqualTo(topic);
        example.setOrderByClause("execution_time asc");
        return BeanCopyUtils.copyListProperties(taskDetailInfoExtendMapper.selectByExample(example), TaskDetailInfoBO::new);
    }

    @Override
    public boolean removeBackup(TaskDetailInfoBO taskDetailInfoBO) {
        long time = taskDetailInfoBO.getExecutionTime().getTime();
        String key = LIST_QUEUE_FORMAT_BACKUP.join(config.getTags().get((int) mod(time, config.getTags().size())));
        return cacheService.lrem(key, JSONUtils.toJSONString(taskDetailInfoBO)) > 0;
    }
}
