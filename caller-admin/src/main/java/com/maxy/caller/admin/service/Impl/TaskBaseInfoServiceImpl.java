package com.maxy.caller.admin.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.maxy.caller.bo.QueryConditionBO;
import com.maxy.caller.bo.TaskBaseInfoBO;
import com.maxy.caller.common.utils.BeanCopyUtils;
import com.maxy.caller.core.service.TaskBaseInfoService;
import com.maxy.caller.model.TaskBaseInfo;
import com.maxy.caller.model.TaskGroup;
import com.maxy.caller.persistent.example.TaskBaseInfoExample;
import com.maxy.caller.persistent.example.TaskGroupExample;
import com.maxy.caller.persistent.mapper.TaskBaseInfoMapper;
import com.maxy.caller.persistent.mapper.TaskGroupMapper;
import com.maxy.caller.pojo.Pagination;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Author maxy
 **/
@Service
public class TaskBaseInfoServiceImpl implements TaskBaseInfoService {
    @Resource
    private TaskBaseInfoMapper taskBaseInfoMapper;
    @Resource
    private TaskGroupMapper taskGroupMapper;

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
        taskDetailInfo.setCreateTime(new Date());
        taskDetailInfo.setUpdateTime(new Date());
        return taskBaseInfoMapper.insertSelective(taskDetailInfo) > 0;
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
        taskBaseInfo.setUpdateTime(new Date());
        return taskBaseInfoMapper.updateByExampleSelective(taskBaseInfo, taskBaseInfoExample) > 0;
    }

    @Override
    public Boolean detele(Long taskInfoId) {
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
        if(CollectionUtils.isEmpty(taskBaseInfos)){
            return null;
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
}
