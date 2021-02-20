package com.maxy.caller.admin.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.maxy.caller.bo.QueryConditionBO;
import com.maxy.caller.bo.TaskGroupBO;
import com.maxy.caller.common.utils.BeanCopyUtils;
import com.maxy.caller.common.utils.DateUtils;
import com.maxy.caller.core.service.TaskGroupService;
import com.maxy.caller.model.TaskGroup;
import com.maxy.caller.persistent.example.TaskGroupExample;
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
public class TaskGroupServiceImpl implements TaskGroupService {
    @Resource
    private TaskGroupMapper taskGroupMapper;

    @Override
    public PageInfo<TaskGroupBO> list(QueryConditionBO queryConditionBO) {
        Pagination pagination = queryConditionBO.getPagination();
        PageHelper.startPage(pagination.getPageNum(), pagination.getPageSize());
        TaskGroupExample example = new TaskGroupExample();
        if (Objects.nonNull(queryConditionBO.getGroupKey())) {
            example.createCriteria().andGroupKeyLike(queryConditionBO.getGroupKey());
        }
        if (Objects.nonNull(queryConditionBO.getBizKey())) {
            example.createCriteria().andBizKeyLike(queryConditionBO.getBizKey());
        }
        List<TaskGroup> taskGroups = taskGroupMapper.selectByExample(example);
        return new PageInfo(BeanCopyUtils.copyListProperties(taskGroups, TaskGroupBO::new));
    }

    @Override
    public Boolean save(TaskGroupBO taskGroupBO) {
        TaskGroup taskGroup = new TaskGroup();
        BeanCopyUtils.copy(taskGroupBO, taskGroup);
        Date date = DateUtils.getNowTime();
        taskGroup.setCreateTime(date);
        taskGroup.setUpdateTime(date);
        return taskGroupMapper.insertSelective(taskGroup) > 0;
    }

    @Override
    public Boolean update(TaskGroupBO taskGroupBO) {
        TaskGroup taskGroup = new TaskGroup();
        BeanCopyUtils.copy(taskGroupBO, taskGroup);
        taskGroup.setUpdateTime(DateUtils.getNowTime());
        TaskGroupExample example = new TaskGroupExample();
        example.createCriteria().andGroupKeyEqualTo(taskGroupBO.getGroupKey()).andBizKeyEqualTo(taskGroupBO.getBizKey());
        return taskGroupMapper.updateByExampleSelective(taskGroup, example) > 0;
    }

    @Override
    public List<TaskGroupBO> getAllList() {
        TaskGroupExample example = new TaskGroupExample();
        List<TaskGroup> taskGroups = taskGroupMapper.selectByExample(example);
        return BeanCopyUtils.copyListProperties(taskGroups, TaskGroupBO::new);
    }

    @Override
    public List<TaskGroupBO> getListByLogRetentionDay(Short days) {
        TaskGroupExample example = new TaskGroupExample();
        example.createCriteria().andLogRetentionDaysGreaterThan(days);
        List<TaskGroup> taskGroups = taskGroupMapper.selectByExample(example);
        return BeanCopyUtils.copyListProperties(taskGroups, TaskGroupBO::new);
    }

    @Override
    public List<TaskGroupBO> getAllListByAddressType(Byte addressType) {
        TaskGroupExample example = new TaskGroupExample();
        example.createCriteria().andAddressTypeEqualTo(addressType);
        List<TaskGroup> taskGroups = taskGroupMapper.selectByExample(example);
        return BeanCopyUtils.copyListProperties(taskGroups, TaskGroupBO::new);
    }

    @Override
    public Boolean deleteByGroupKeyAndBizKey(String groupKey, String bizKey) {
        TaskGroupExample example = new TaskGroupExample();
        example.createCriteria().andGroupKeyEqualTo(groupKey).andBizKeyEqualTo(bizKey);
        return taskGroupMapper.deleteByExample(example) > 0;
    }

    @Override
    public TaskGroupBO getByGroupKeyAndBizKey(String groupKey, String bizKey) {
        TaskGroupExample example = new TaskGroupExample();
        example.createCriteria().andGroupKeyEqualTo(groupKey).andBizKeyEqualTo(bizKey);
        List<TaskGroup> taskGroups = taskGroupMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(taskGroups)) {
            return null;
        }
        TaskGroupBO taskGroupBO = new TaskGroupBO();
        BeanUtils.copyProperties(taskGroups.get(0), taskGroupBO);
        return taskGroupBO;
    }

    @Override
    public TaskGroupBO updateStatus(TaskGroupBO taskGroupBO, Byte sourceStatus, Byte targetStatus) {
        TaskGroupExample example = new TaskGroupExample();
        example.createCriteria().andGroupKeyEqualTo(taskGroupBO.getGroupKey())
                .andBizKeyEqualTo(taskGroupBO.getBizKey())
                .andStatusEqualTo(sourceStatus);
        TaskGroup taskGroup = new TaskGroup();
        BeanUtils.copyProperties(taskGroupBO, taskGroup);
        taskGroup.setStatus(targetStatus);
        taskGroup.setUpdateTime(DateUtils.getNowTime());
        if (taskGroupMapper.updateByExampleSelective(taskGroup, example) > 0) {
            return taskGroupBO;
        }
        return null;
    }


}
