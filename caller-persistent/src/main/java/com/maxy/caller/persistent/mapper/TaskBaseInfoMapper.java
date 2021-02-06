package com.maxy.caller.persistent.mapper;

import com.maxy.caller.model.TaskBaseInfo;
import com.maxy.caller.persistent.example.TaskBaseInfoExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskBaseInfoMapper {
    long countByExample(TaskBaseInfoExample example);

    int deleteByExample(TaskBaseInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TaskBaseInfo record);

    int insertSelective(TaskBaseInfo record);

    List<TaskBaseInfo> selectByExample(TaskBaseInfoExample example);

    TaskBaseInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TaskBaseInfo record, @Param("example") TaskBaseInfoExample example);

    int updateByExample(@Param("record") TaskBaseInfo record, @Param("example") TaskBaseInfoExample example);

    int updateByPrimaryKeySelective(TaskBaseInfo record);

    int updateByPrimaryKey(TaskBaseInfo record);
}