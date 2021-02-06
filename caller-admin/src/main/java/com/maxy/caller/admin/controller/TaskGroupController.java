package com.maxy.caller.admin.controller;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.maxy.caller.admin.template.ExecuteLog;
import com.maxy.caller.admin.template.FacadeTemplate;
import com.maxy.caller.admin.template.Response;
import com.maxy.caller.bo.QueryConditionBO;
import com.maxy.caller.bo.TaskGroupBO;
import com.maxy.caller.common.utils.BeanCopyUtils;
import com.maxy.caller.core.service.TaskGroupService;
import com.maxy.caller.vo.QueryConditionVO;
import com.maxy.caller.vo.TaskBaseInfoVO;
import com.maxy.caller.vo.TaskGroupVO;
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
public class TaskGroupController {

    @Resource
    private TaskGroupService taskGroupService;

    @GetMapping("/task/group/list")
    public Response<PageInfo<TaskGroupVO>> list(QueryConditionVO queryConditionVO) {
        return FacadeTemplate.execute(ExecuteLog.newEntity("list", queryConditionVO), () -> {

        }, () -> {
            QueryConditionBO queryConditionBO = new QueryConditionBO();
            BeanUtils.copyProperties(queryConditionVO, queryConditionBO);
            List<TaskGroupBO> list = taskGroupService.list(queryConditionBO).getList();
            List<TaskGroupVO> taskGroupVOList = BeanCopyUtils.copyListProperties(list, TaskGroupVO::new);
            return Response.getSuccessResponse(new PageInfo<>(taskGroupVOList));
        });
    }

    @PostMapping("/task/group/save")
    public Response<Map<String, Boolean>> save(@RequestBody TaskGroupVO taskGroupVO) {
        return FacadeTemplate.execute(ExecuteLog.newEntity("save", taskGroupVO), () -> {
        }, () -> {
            Map<String, Boolean> map = Maps.newHashMap();
            TaskGroupBO taskGroupBO = new TaskGroupBO();
            BeanCopyUtils.copy(taskGroupVO, taskGroupBO);
            map.put("result", taskGroupService.save(taskGroupBO));
            return Response.getSuccessResponse(map);
        });
    }

    @PostMapping("/task/group/update")
    public Response<Map<String, Boolean>> update(@RequestBody TaskGroupVO taskGroupVO) {
        return FacadeTemplate.execute(ExecuteLog.newEntity("update", taskGroupVO), () -> {

        }, () -> {
            Map<String, Boolean> map = Maps.newHashMap();
            TaskGroupBO taskGroupBO = new TaskGroupBO();
            BeanCopyUtils.copy(taskGroupVO, taskGroupBO);
            map.put("result", taskGroupService.update(taskGroupBO));
            return Response.getSuccessResponse(map);
        });
    }

    @GetMapping("/task/group/deleteByGroupKeyAndBizKey")
    public Response<Map<String, Boolean>> delete(String groupKey, String bizKey) {
        return FacadeTemplate.execute(ExecuteLog.newEntity("delete", groupKey, bizKey), () -> {
        }, () -> {
            Map<String, Boolean> map = Maps.newHashMap();
            map.put("result", taskGroupService.deleteByGroupKeyAndBizKey(groupKey,bizKey));
            return Response.getSuccessResponse(map);
        });
    }

    @GetMapping("/task/group/getByGroupKeyAndBizKey")
    public Response<TaskBaseInfoVO> getByGroupKeyAndBizKey(String groupKey, String bizKey) {
        return FacadeTemplate.execute(ExecuteLog.newEntity("getById", groupKey,bizKey), () -> {
        }, () -> {
            TaskGroupBO taskGroupBO = taskGroupService.getByGroupKeyAndBizKey(groupKey, bizKey);
            TaskBaseInfoVO taskBaseInfoVO = new TaskBaseInfoVO();
            BeanCopyUtils.copy(taskGroupBO, taskBaseInfoVO);
            return Response.getSuccessResponse(taskBaseInfoVO);
        });
    }
}
