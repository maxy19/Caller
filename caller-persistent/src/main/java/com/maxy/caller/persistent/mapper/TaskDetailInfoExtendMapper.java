package com.maxy.caller.persistent.mapper;

import com.maxy.caller.model.TaskDetailInfo;
import com.maxy.caller.persistent.example.TaskDetailInfoExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskDetailInfoExtendMapper extends TaskDetailInfoMapper {

    void batchInsert(List<TaskDetailInfo> taskDetailInfoList);

    List<TaskDetailInfo> getPreReadInfo(TaskDetailInfoExample example);

    int updateStatusByIds(@Param("ids") List<Long> ids, @Param("targetStatus") Byte targetStatus,@Param("sourceStatus") Byte sourceStatus);
}