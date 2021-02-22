package com.maxy.caller.admin.service;


import com.maxy.caller.core.service.CommonService;

/**
 * @Author maxy
 **/
public interface AdminWorker extends CommonService {

    /**
     * 开始
     */
    void start();

    /**
     * 关闭销毁
     */
    void stop();

}
