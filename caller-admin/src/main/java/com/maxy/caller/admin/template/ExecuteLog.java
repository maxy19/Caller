package com.maxy.caller.admin.template;


import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;

/**
 * maxy
 */
@Getter
@Setter
@Log4j2
public class ExecuteLog<T> {


    private final String methodName;

    private final T[] request;

    public ExecuteLog(String methodName, T... request) {
        this.methodName = methodName;
        this.request = request;
    }

    public void startLog() {
        log.info("方法:[{}]_开始#参数:[{}]", methodName, requestParams());
    }

    @SuppressWarnings("rawtypes")
    public void endLog(Response response, long time) {
        log.info("方法:[{}]_结束#耗时:[{}] ms#response:[{}]", methodName, time, response);
    }

    public void printLog(Exception e) {
        log.error(String.format("执行 %s 错误! 参数[%s]", methodName, requestParams()), e);
    }

    public void printMsgLog(Exception e) {
        log.error(String.format("执行 %s 错误! 参数:[%s]", methodName, requestParams()), e.getMessage());
    }

    public static <T> ExecuteLog<T> newEntity(String methodName, T... request) {
        return new ExecuteLog<T>(methodName, request);
    }

    private Object requestParams() {
        return ArrayUtils.isEmpty(request) ? request : Lists.newArrayList(request);
    }

}
