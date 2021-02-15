package com.maxy.caller.admin.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.maxy.caller.bo.QueryConditionBO;
import com.maxy.caller.bo.TaskDetailInfoBO;
import com.maxy.caller.common.utils.BeanCopyUtils;
import com.maxy.caller.common.utils.DateUtils;
import com.maxy.caller.core.service.TaskBaseInfoService;
import com.maxy.caller.core.service.TaskDetailInfoService;
import com.maxy.caller.model.TaskDetailInfo;
import com.maxy.caller.model.TaskGroup;
import com.maxy.caller.persistent.example.TaskDetailInfoExample;
import com.maxy.caller.persistent.example.TaskGroupExample;
import com.maxy.caller.persistent.mapper.TaskDetailInfoExtendMapper;
import com.maxy.caller.persistent.mapper.TaskGroupMapper;
import com.maxy.caller.pojo.Pagination;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    private TaskBaseInfoService taskBaseInfoService;

    @Override
    public PageInfo<TaskDetailInfoBO> list(QueryConditionBO queryConditionBO) {
        Pagination pagination = queryConditionBO.getPagination();
        PageHelper.startPage(pagination.getPageNum(), pagination.getPageSize());
        TaskGroupExample example = new TaskGroupExample();
        example.createCriteria().andGroupKeyEqualTo(queryConditionBO.getGroupKey())
                .andBizKeyEqualTo(queryConditionBO.getBizKey());
        List<TaskGroup> taskGroups = taskGroupMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(taskGroups)) {
            TaskGroup taskGroup = taskGroups.stream().filter(Objects::nonNull).findFirst().get();
            TaskDetailInfoExample taskDetailInfoExample = new TaskDetailInfoExample();
            taskDetailInfoExample.createCriteria().andIdEqualTo(taskGroup.getId());
            List<TaskDetailInfo> taskDetailInfos = taskDetailInfoExtendMapper.selectByExample(taskDetailInfoExample);
            return new PageInfo(BeanCopyUtils.copyListProperties(taskDetailInfos, TaskDetailInfoBO::new));
        }
        return new PageInfo<>();
    }

    @Override
    public Boolean save(TaskDetailInfoBO taskDetailInfoBO) {
        TaskDetailInfo taskDetailInfo = new TaskDetailInfo();
        BeanCopyUtils.copy(taskDetailInfoBO, taskDetailInfo);
        taskDetailInfo.setCreateTime(new Date());
        taskDetailInfo.setUpdateTime(new Date());
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
        taskDetailInfo.setUpdateTime(new Date());
        return taskDetailInfoExtendMapper.updateByExampleSelective(taskDetailInfo, taskDetailInfoExample) > 0;
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

    public List<TaskDetailInfoBO> getTaskDetailInfoBOList(Date currentTime, Byte executionStatus, int addSecond, int minusSecond) {
        TaskDetailInfoExample example = new TaskDetailInfoExample();
        example.createCriteria().andExecutionTimeBetween(DateUtils.addSecond(currentTime, minusSecond), DateUtils.addSecond(currentTime, addSecond));
        example.createCriteria().andExecutionStatusEqualTo(executionStatus);
        List<TaskDetailInfo> taskDetailInfos = taskDetailInfoExtendMapper.selectByExample(example);
        return BeanCopyUtils.copyListProperties(taskDetailInfos, TaskDetailInfoBO::new);
    }

    public List<TaskDetailInfoBO> getTaskDetailInfoBOList(Date currentTime, Byte executionStatus, int addSecond) {
        TaskDetailInfoExample example = new TaskDetailInfoExample();
        example.createCriteria().andExecutionTimeBetween(currentTime, DateUtils.addSecond(currentTime, addSecond));
        example.createCriteria().andExecutionStatusEqualTo(executionStatus);
        List<TaskDetailInfo> taskDetailInfos = taskDetailInfoExtendMapper.selectByExample(example);
        return BeanCopyUtils.copyListProperties(taskDetailInfos, TaskDetailInfoBO::new);
    }

    @Override
    public List<TaskDetailInfoBO> getPreReadInfo(Byte status, Date startTime, Date endTime) {
        TaskDetailInfoExample example = new TaskDetailInfoExample();
        example.createCriteria().andExecutionStatusEqualTo(status);
        example.createCriteria().andExecutionTimeGreaterThan(startTime);
        example.createCriteria().andExecutionTimeLessThanOrEqualTo(endTime);
        List<TaskDetailInfo> taskDetailInfos = taskDetailInfoExtendMapper.getPreReadInfo(example);
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
    public TaskDetailInfoBO get(String groupKey, String bizKey, String topic, Date executionTime) {
        TaskDetailInfoExample example = new TaskDetailInfoExample();
        example.createCriteria().andGroupKeyEqualTo(groupKey).andBizKeyEqualTo(bizKey)
                .andTopicEqualTo(topic).andExecutionTimeEqualTo(executionTime);
        List<TaskDetailInfo> taskDetailInfoList = taskDetailInfoExtendMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(taskDetailInfoList)) {
            TaskDetailInfoBO taskDetailInfoBO = new TaskDetailInfoBO();
            BeanUtils.copyProperties(taskDetailInfoList.get(0), taskDetailInfoBO);
            return taskDetailInfoBO;
        }
        return null;
    }
    /**
     * 缓存路由策略
     *
     * @return
     */
    @Override
    public Byte getRouterStrategy(TaskDetailInfoBO taskDetailInfoBO) {
        return taskBaseInfoService.getRouterStrategy(taskDetailInfoBO.getGroupKey(), taskDetailInfoBO.getBizKey(), taskDetailInfoBO.getTopic());
    }
}
