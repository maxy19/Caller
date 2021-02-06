package com.maxy.caller.core.service;

import com.maxy.caller.bo.TaskRegistryBO;

import java.util.List;

/**
 * @Author maxy
 **/
public interface TaskRegistryService {

    List<TaskRegistryBO> getAllRegistry();

    void delete(List<TaskRegistryBO> registryBOList);

    boolean save(TaskRegistryBO taskRegistryBO);

    boolean deleteByNotActive(String groupKey, String bizKey, String address);
}
