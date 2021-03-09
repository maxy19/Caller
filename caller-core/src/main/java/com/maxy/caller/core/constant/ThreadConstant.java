package com.maxy.caller.core.constant;

/**
 * @Author maxuyang
 **/
public interface ThreadConstant {

       String INVOKE_CLIENT_TASK_THREAD_POOL = "invoke-client-method-thread-pool";
       String RETRY_TASK_THREAD_POOL = "retry-invoke-client-method-thread-pool";
       String POP_LOOP_SLOT_THREAD_POOL = "pop-loop-slot-thread-pool";
       String CLIENT_INVOKE_METHOD_THREAD_POOL = "client-invoke-method-thread-pool";
       String CLIENT_HANDLE_THREAD_POOL = "caller-netty-client-handle-thread-%d";
       String SERVER_HANDLE_THREAD_POOL = "caller-netty-server-handle-thread-%d";

}
