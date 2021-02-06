package com.maxy.caller.admin.controller;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.maxy.caller.admin.template.ExecuteLog;
import com.maxy.caller.admin.template.FacadeTemplate;
import com.maxy.caller.admin.template.Response;
import com.maxy.caller.bo.QueryConditionBO;
import com.maxy.caller.bo.TaskBaseInfoBO;
import com.maxy.caller.common.utils.BeanCopyUtils;
import com.maxy.caller.core.service.TaskBaseInfoService;
import com.maxy.caller.vo.QueryConditionVO;
import com.maxy.caller.vo.TaskBaseInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author maxy
 **/
@Controller
@RestController
public class TaskBaseInfoController {

    @Resource
    private TaskBaseInfoService taskBaseInfoService;

    @GetMapping("/task/base/info/getListByGroup")
    public Response<PageInfo<TaskBaseInfoVO>> getListByGroup(QueryConditionVO queryConditionVO) {
        return FacadeTemplate.execute(ExecuteLog.newEntity("getListByGroup", queryConditionVO), () -> {
        }, () -> {
            QueryConditionBO queryConditionBO = new QueryConditionBO();
            BeanUtils.copyProperties(queryConditionVO, queryConditionBO);
            List<TaskBaseInfoBO> list = taskBaseInfoService.list(queryConditionBO).getList();
            return Response.getSuccessResponse(new PageInfo<>(BeanCopyUtils.copyListProperties(list, TaskBaseInfoVO::new)));
        });
    }

    @PostMapping("/task/base/info/save")
    public Response<Map<String, Boolean>> save(@RequestBody TaskBaseInfoVO taskBaseInfoVO) {
        return FacadeTemplate.execute(ExecuteLog.newEntity("save", taskBaseInfoVO), () -> {
        }, () -> {
            Map<String, Boolean> map = Maps.newHashMap();
            TaskBaseInfoBO taskBaseInfoBO = new TaskBaseInfoBO();
            BeanCopyUtils.copy(taskBaseInfoVO, taskBaseInfoBO);
            map.put("result", taskBaseInfoService.save(taskBaseInfoBO));
            return Response.getSuccessResponse(map);
        });
    }

    @PostMapping("/task/base/info/update")
    public Response<Map<String, Boolean>> update(@RequestBody TaskBaseInfoVO TaskBaseInfoVO) {
        return FacadeTemplate.execute(ExecuteLog.newEntity("update", TaskBaseInfoVO), () -> {
        }, () -> {
            Map<String, Boolean> map = Maps.newHashMap();
            TaskBaseInfoBO TaskBaseInfoBO = new TaskBaseInfoBO();
            BeanCopyUtils.copy(TaskBaseInfoVO, TaskBaseInfoBO);
            map.put("result", taskBaseInfoService.update(TaskBaseInfoBO));
            return Response.getSuccessResponse(map);
        });
    }

    @GetMapping("/task/base/info/delete")
    public Response<Map<String, Boolean>> delete(Long taskInfoId) {
        return FacadeTemplate.execute(ExecuteLog.newEntity("delete", taskInfoId), () -> {
        }, () -> {
            Map<String, Boolean> map = Maps.newHashMap();
            map.put("result", taskBaseInfoService.detele(taskInfoId));
            return Response.getSuccessResponse(map);
        });
    }

}
