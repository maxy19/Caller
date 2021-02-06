package com.maxy.caller.persistent.mapper;

import com.maxy.caller.model.TaskDetailInfo;
import com.maxy.caller.persistent.example.TaskDetailInfoExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskDetailInfoMapper {
    long countByExample(TaskDetailInfoExample example);

    int deleteByExample(TaskDetailInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TaskDetailInfo record);

    int insertSelective(TaskDetailInfo record);

    List<TaskDetailInfo> selectByExample(TaskDetailInfoExample example);

    TaskDetailInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TaskDetailInfo record, @Param("example") TaskDetailInfoExample example);

    int updateByExample(@Param("record") TaskDetailInfo record, @Param("example") TaskDetailInfoExample example);

    int updateByPrimaryKeySelective(TaskDetailInfo record);

    int updateByPrimaryKey(TaskDetailInfo record);
}