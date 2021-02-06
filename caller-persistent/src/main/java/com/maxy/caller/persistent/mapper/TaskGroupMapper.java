package com.maxy.caller.persistent.mapper;

import com.maxy.caller.model.TaskGroup;
import com.maxy.caller.persistent.example.TaskGroupExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskGroupMapper {
    long countByExample(TaskGroupExample example);

    int deleteByExample(TaskGroupExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TaskGroup record);

    int insertSelective(TaskGroup record);

    List<TaskGroup> selectByExample(TaskGroupExample example);

    TaskGroup selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TaskGroup record, @Param("example") TaskGroupExample example);

    int updateByExample(@Param("record") TaskGroup record, @Param("example") TaskGroupExample example);

    int updateByPrimaryKeySelective(TaskGroup record);

    int updateByPrimaryKey(TaskGroup record);
}