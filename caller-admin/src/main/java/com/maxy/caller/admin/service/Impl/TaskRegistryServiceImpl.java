package com.maxy.caller.admin.service.Impl;

import com.maxy.caller.bo.TaskRegistryBO;
import com.maxy.caller.common.utils.BeanCopyUtils;
import com.maxy.caller.core.service.TaskRegistryService;
import com.maxy.caller.model.TaskRegistry;
import com.maxy.caller.persistent.example.TaskRegistryExample;
import com.maxy.caller.persistent.mapper.TaskRegistryMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Author maxy
 **/
@Service
public class TaskRegistryServiceImpl implements TaskRegistryService {

    @Resource
    private TaskRegistryMapper taskRegistryMapper;


    @Override
    public List<TaskRegistryBO> getAllRegistry() {
        TaskRegistryExample example = new TaskRegistryExample();
        List<TaskRegistry> taskRegistries = taskRegistryMapper.selectByExample(example);
        return BeanCopyUtils.copyListProperties(taskRegistries, TaskRegistryBO::new);
    }


    @Override
    public void delete(List<TaskRegistryBO> registryBOList) {
        registryBOList.forEach(taskRegistryBO -> {
            TaskRegistryExample example = new TaskRegistryExample();
            example.createCriteria().andGroupKeyEqualTo(taskRegistryBO.getGroupKey());
            example.createCriteria().andBizKeyEqualTo(taskRegistryBO.getBizKey());
            taskRegistryMapper.deleteByExample(example);
        });
    }

    @Override
    public boolean save(TaskRegistryBO taskRegistryBO) {
        TaskRegistry registry = new TaskRegistry();
        BeanUtils.copyProperties(taskRegistryBO, registry);
        registry.setCreateTime(new Date());
        return taskRegistryMapper.insert(registry) > 0;
    }

    @Override
    public boolean deleteByNotActive(String groupKey, String bizKey, String address) {
        TaskRegistryExample example = new TaskRegistryExample();
        example.createCriteria().andGroupKeyEqualTo(groupKey);
        example.createCriteria().andBizKeyEqualTo(bizKey);
        example.createCriteria().andRegistryAddressEqualTo(address);
        return taskRegistryMapper.deleteByExample(example) > 0;
    }
}
