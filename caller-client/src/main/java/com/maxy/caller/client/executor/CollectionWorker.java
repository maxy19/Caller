package com.maxy.caller.client.executor;

import com.maxy.caller.core.config.ThreadPoolConfig;
import com.maxy.caller.core.config.ThreadPoolRegisterCenter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

/**
 * @Author maxy
 **/
@Component
public class CollectionWorker implements ClientWorker  {

    private ExecutorService collectionWorker = ThreadPoolConfig.getInstance().getSingleThreadExecutor(true);


    @Override
    public void start() {
        collectionWorker.execute(() -> {


        });
    }

    @Override
    public void stop() {
        ThreadPoolRegisterCenter.destroy(collectionWorker);
    }
}
