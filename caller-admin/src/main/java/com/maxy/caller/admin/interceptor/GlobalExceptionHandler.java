package com.maxy.caller.admin.interceptor;


import com.maxy.caller.admin.template.Response;
import com.maxy.caller.core.enums.ExceptionEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author maxy
 */
@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    /**
     * 将业务未拦截的异常最最后兜底处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Response<String> ExceptionHandle(Exception e) {
        log.error("全局拦截捕获异常.",e);
        if (e instanceof IllegalArgumentException || e instanceof IllegalStateException) {
            return Response.error(ExceptionEnum.PARAM_ERROR);
        } else {
            return Response.error(ExceptionEnum.SYS_ERROR);
        }
    }
}