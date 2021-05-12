package com.maxy.caller.client.executor;

import com.maxy.caller.dto.ResultDTO;

/**
 * @Author maxy
 **/
public interface DelayHandler<T> {
    /**
     * 参数为注册的json数据
     * @param param
     * @throws Exception
     */
    ResultDTO<T> execute(String param) throws Exception;

}
