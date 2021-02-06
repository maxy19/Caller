package com.maxy.caller.persistent.mapper;

import com.maxy.caller.model.TaskLog;
import com.maxy.caller.persistent.example.TaskLogExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskLogMapper {
    long countByExample(TaskLogExample example);

    int deleteByExample(TaskLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TaskLog record);

    int insertSelective(TaskLog record);

    List<TaskLog> selectByExample(TaskLogExample example);

    TaskLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TaskLog record, @Param("example") TaskLogExample example);

    int updateByExample(@Param("record") TaskLog record, @Param("example") TaskLogExample example);

    int updateByPrimaryKeySelective(TaskLog record);

    int updateByPrimaryKey(TaskLog record);
}