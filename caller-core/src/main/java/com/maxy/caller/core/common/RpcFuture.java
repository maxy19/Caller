package com.maxy.caller.core.common;

import io.netty.util.concurrent.Promise;
import lombok.Data;

/**
 * @author maxuyang
 */
@Data
public class RpcFuture<T> {
    private Promise<T> promise;
    private long timeout;

    public RpcFuture(Promise<T> promise, long timeout) {
        this.promise = promise;
        this.timeout = timeout;
    }
}
