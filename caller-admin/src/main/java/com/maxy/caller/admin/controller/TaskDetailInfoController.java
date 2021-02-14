package com.maxy.caller.admin.controller;

import com.google.common.collect.Maps;
import com.maxy.caller.admin.template.ExecuteLog;
import com.maxy.caller.admin.template.FacadeTemplate;
import com.maxy.caller.admin.template.Response;
import com.maxy.caller.bo.TaskDetailInfoBO;
import com.maxy.caller.common.utils.BeanCopyUtils;
import com.maxy.caller.core.service.TaskDetailInfoService;
import com.maxy.caller.vo.TaskDetailInfoVO;
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
public class TaskDetailInfoController {

    @Resource
    private TaskDetailInfoService taskDetailInfoService;

    @PostMapping("/task/detail/info/update")
    public Response<Map<String, Boolean>> update(@RequestBody TaskDetailInfoVO taskDetailInfoVO) {
        return FacadeTemplate.execute(ExecuteLog.newEntity("update", taskDetailInfoVO), () -> {

        }, () -> {
            Map<String, Boolean> map = Maps.newHashMap();
            TaskDetailInfoBO taskDetailInfoBO = new TaskDetailInfoBO();
            BeanCopyUtils.copy(taskDetailInfoVO, taskDetailInfoBO);
            map.put("result", taskDetailInfoService.update(taskDetailInfoBO));
            return Response.getSuccessResponse(map);
        });
    }

    @GetMapping("/task/detail/info/delete")
    public Response<Map<String, Boolean>> delete(Long taskDetailId) {
        return FacadeTemplate.execute(ExecuteLog.newEntity("delete", taskDetailId), () -> {
        }, () -> {
            Map<String, Boolean> map = Maps.newHashMap();
            map.put("result", taskDetailInfoService.delete(taskDetailId));
            return Response.getSuccessResponse(map);
        });
    }


    @GetMapping("/task/detail/info/getTaskDetailList")
    public Response<List<TaskDetailInfoVO>> getTaskDetailList(String groupKey, String bizKey, String topic) {
        return FacadeTemplate.execute(ExecuteLog.newEntity("getTaskDetailList", groupKey, bizKey, topic), () -> {
        }, () -> {
            List<TaskDetailInfoBO> taskDetailInfoBOList = taskDetailInfoService.getTaskDetailList(groupKey, bizKey, topic);
            List<TaskDetailInfoVO> taskDetailInfoVOS = BeanCopyUtils.copyListProperties(taskDetailInfoBOList, TaskDetailInfoVO::new);
            return Response.getSuccessResponse(taskDetailInfoVOS);
        });
    }


}
