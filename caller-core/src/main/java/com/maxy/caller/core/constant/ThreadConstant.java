package com.maxy.caller.core.constant;

/**
 * @Author maxuyang
 **/
public interface ThreadConstant {

       String INVOKE_CLIENT_TASK_THREAD_POOL = "invoke-client-method-thread-pool";
       String RETRY_TASK_THREAD_POOL = "retry-invoke-client-method-thread-pool";
       String SERVER_RING_BUFFER_THREAD_POOL = "server-ring-buffer-thread-pool";
       String SERVER_SAVE_REG_THREAD_POOL = "server-save-reg-thread-pool";
       String SERVER_HEART_RESP_THREAD_POOL = "server-heart-resp-thread-pool";
       String SERVER_SAVE_DELAY_TASK_THREAD_POOL = "server-save-delay-task-thread-pool";
       String SERVER_RESP_RESULT_THREAD_POOL = "server-resp-result-thread-pool";
       String POP_LOOP_SLOT_THREAD_POOL = "pop-loop-slot-thread-pool";
       String POP_LOOP_SLOT_GROUP_THREAD_POOL = "pop-loop-slot-group-thread-pool";
       String CLIENT_INVOKE_METHOD_THREAD_POOL = "client-invoke-method-thread-pool";
       String CLIENT_HANDLE_THREAD_POOL = "caller-netty-client-handle-thread-%d";
       String SERVER_HANDLE_THREAD_POOL = "caller-netty-server-handle-thread-%d";
       String ADMIN_FLOW_THREAD_POOL = "admin-flow-thread-pool";
       String ADMIN_TRIGGER_WORKER_THREAD_POOL = "admin-trigger-worker-thread-pool";
       String ADMIN_TRIGGER_BACKUP_WORKER_THREAD_POOL = "admin-trigger-backup-worker-thread-pool";
       String ADMIN_SCHEDULE_WORKER_THREAD_POOL = "admin-schedule-worker-thread-pool";
}
