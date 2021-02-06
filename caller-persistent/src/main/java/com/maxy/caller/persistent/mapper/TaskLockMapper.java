package com.maxy.caller.persistent.mapper;

import com.maxy.caller.model.TaskLock;
import com.maxy.caller.persistent.example.TaskLockExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskLockMapper {

    long countByExample(TaskLockExample example);

    int deleteByExample(TaskLockExample example);

    int deleteByPrimaryKey(String lockName);

    int insert(TaskLock record);

    int insertSelective(TaskLock record);

    List<TaskLock> selectByExample(TaskLockExample example);

    int updateByExampleSelective(@Param("record") TaskLock record, @Param("example") TaskLockExample example);

    int updateByExample(@Param("record") TaskLock record, @Param("example") TaskLockExample example);

    String lockForUpdate();
}