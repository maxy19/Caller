package com.maxy.caller.persistent.mapper;

import com.maxy.caller.model.TaskRegistry;
import com.maxy.caller.persistent.example.TaskRegistryExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskRegistryMapper {
    long countByExample(TaskRegistryExample example);

    int deleteByExample(TaskRegistryExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TaskRegistry record);

    int insertSelective(TaskRegistry record);

    List<TaskRegistry> selectByExample(TaskRegistryExample example);

    TaskRegistry selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TaskRegistry record, @Param("example") TaskRegistryExample example);

    int updateByExample(@Param("record") TaskRegistry record, @Param("example") TaskRegistryExample example);

    int updateByPrimaryKeySelective(TaskRegistry record);

    int updateByPrimaryKey(TaskRegistry record);
}