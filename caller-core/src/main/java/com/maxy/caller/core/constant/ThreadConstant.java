package com.maxy.caller.core.constant;

/**
 * @Author maxuyang
 **/
public interface ThreadConstant {

       String INVOKE_CLIENT_TASK_THREAD_POOL = "invoke_client_task_thread_pool";
       String RETRY_TASK_THREAD_POOL = "retry_task_thread_pool";
       String CLIENT_HANDLE_THREAD_POOL = "caller-netty-client-handle-thread_%d";
       String SERVER_HANDLE_THREAD_POOL = "caller-netty-server-handle-thread_%d";

}
