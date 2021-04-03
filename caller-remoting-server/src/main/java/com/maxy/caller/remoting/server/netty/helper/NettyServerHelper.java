package com.maxy.caller.remoting.server.netty.helper;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.maxy.caller.bo.TaskRegistryBO;
import com.maxy.caller.core.common.RpcFuture;
import com.maxy.caller.core.config.ThreadPoolConfig;
import com.maxy.caller.core.config.ThreadPoolRegisterCenter;
import com.maxy.caller.core.enums.MsgTypeEnum;
import com.maxy.caller.core.netty.pojo.Pinger;
import com.maxy.caller.core.netty.protocol.ProtocolMsg;
import com.maxy.caller.core.service.TaskRegistryService;
import com.maxy.caller.dto.RpcRequestDTO;
import com.maxy.caller.pojo.RegConfigInfo;
import com.maxy.caller.remoting.server.netty.mpmc.RingbufferInvoker;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static com.maxy.caller.core.common.RpcHolder.REQUEST_MAP;
import static com.maxy.caller.core.constant.ThreadConstant.SERVER_HEART_RESP_THREAD_POOL;
import static com.maxy.caller.core.constant.ThreadConstant.SERVER_RESP_RESULT_THREAD_POOL;
import static com.maxy.caller.core.constant.ThreadConstant.SERVER_SAVE_DELAY_TASK_THREAD_POOL;
import static com.maxy.caller.core.constant.ThreadConstant.SERVER_SAVE_REG_THREAD_POOL;
import static com.maxy.caller.core.utils.CallerUtils.parse;

/**
 * @author maxy
 */
@Log4j2
@Data
@Component
public class NettyServerHelper {

    @Resource
    private TaskRegistryService taskRegistryService;
    @Resource
    private RingbufferInvoker ringbufferInvoker;
    /**
     * key:group+biz
     * value:channel
     */
    private ListMultimap<String, Channel> activeChannel = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());
    /**
     * ip+channel
     */
    private Map<String, Channel> ipChannelMapping = Maps.newConcurrentMap();

    public Channel getChannelByAddr(String address) {
        return ipChannelMapping.get(address);
    }

    /**
     * 各种事件处理器
     */
    private Map<MsgTypeEnum, BiConsumer<ProtocolMsg, Channel>> eventMap = new ConcurrentHashMap();
    private ExecutorService saveRegExecutor = ThreadPoolConfig.getInstance().getSingleThreadExecutor(false, SERVER_SAVE_REG_THREAD_POOL);
    private ExecutorService heartRespExecutor = ThreadPoolConfig.getInstance().getSingleThreadExecutor(false, SERVER_HEART_RESP_THREAD_POOL);
    private ExecutorService saveDelayTaskExecutor = ThreadPoolConfig.getInstance().getPublicThreadPoolExecutor(false, SERVER_SAVE_DELAY_TASK_THREAD_POOL);
    private ExecutorService respResultExecutor = ThreadPoolConfig.getInstance().getPublicThreadPoolExecutor(false, SERVER_RESP_RESULT_THREAD_POOL);

    /**
     * 服务端客户端共有事件
     */ {
        eventMap.put(MsgTypeEnum.MESSAGE, (protocolMsg, channel) -> {
            String msg = (String) getRequest(protocolMsg);
            log.info("消息:{}", msg);
        });
    }

    /**
     * 服务端解析客户端事件
     */
    public Supplier<NettyServerHelper> registryEvent = () -> {
        eventMap.put(MsgTypeEnum.REGISTRY, (protocolMsg, channel) -> {
            saveRegExecutor.execute(() -> {
                RpcRequestDTO request = (RpcRequestDTO) getRequest(protocolMsg);
                RegConfigInfo regConfigInfo = request.getRegConfigInfo();
                activeChannel.put(regConfigInfo.getUniqName(), channel);
                ipChannelMapping.put(parse(channel), channel);
                //去掉不活跃的
                removeNotActive(regConfigInfo.getUniqName());
                //保存register
                TaskRegistryBO taskRegistryBO = new TaskRegistryBO();
                BeanUtils.copyProperties(regConfigInfo, taskRegistryBO);
                taskRegistryBO.setRegistryAddress(parse(channel));
                taskRegistryService.save(taskRegistryBO);
                channel.writeAndFlush(ProtocolMsg.toEntity(regConfigInfo.getUniqName() + "注册成功!"));
            });
        });
        return this;
    };

    /**
     * 服务端解析客户端事件
     */
    public Supplier<NettyServerHelper> heartbeatEvent = () -> {
        eventMap.put(MsgTypeEnum.HEARTBEAT, (protocolMsg, channel) -> {
            heartRespExecutor.execute(() -> {
                Pinger pinger = (Pinger) getRequest(protocolMsg);
                channel.writeAndFlush(ProtocolMsg.toEntity("服务端收到客户端的心跳消息!"));
                removeNotActive(pinger.getUniqueName());
            });
        });
        return this;
    };


    /**
     * 服务端解析客户端事件
     */
    public Supplier<NettyServerHelper> resultEvent = () -> {
        eventMap.put(MsgTypeEnum.RESULT, (protocolMsg, channel) -> {
            respResultExecutor.execute(() -> {
                Stopwatch stopwatch = Stopwatch.createStarted();
                log.info("resultEvent#执行客户端方法返回值:{}", protocolMsg);
                RpcFuture<ProtocolMsg> rpcFuture = REQUEST_MAP.get(protocolMsg.getRequestId());
                Preconditions.checkArgument(rpcFuture != null, "通过reqId没有找到对应的future信息..");
                rpcFuture.getPromise().setSuccess(protocolMsg);
                REQUEST_MAP.remove(protocolMsg.getRequestId());
                log.info("resultEvent:耗时:reqId:{},{}", protocolMsg.getRequestId(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
            });
        });
        return this;
    };

    /**
     * 服务端解析客户端事件
     */
    public Supplier<NettyServerHelper> delayTaskEvent = () -> {
        eventMap.put(MsgTypeEnum.DELAYTASK, (protocolMsg, channel) -> {
            RpcRequestDTO request = (RpcRequestDTO) getRequest(protocolMsg);
            ringbufferInvoker.invoke(request.getDelayTasks(),parse(channel));
            log.debug("delayTaskEvent#接受客户端添加延迟任务:{}", request.getDelayTasks());
        });
        return this;
    };

    private Object getRequest(ProtocolMsg protocolMsg) {
        Object request = protocolMsg.getBody();
        Preconditions.checkArgument(Objects.nonNull(request));
        return request;
    }


    private void removeNotActive(String uniqueName) {
        //去掉不活跃的
        List<Channel> collection = Lists.newArrayList();
        List<Channel> channels = activeChannel.get(uniqueName);
        if (CollectionUtils.isEmpty(channels)) {
            return;
        }
        channels.removeIf(socketChannel -> {
            if (!socketChannel.isActive()) {
                collection.add(socketChannel);
                ipChannelMapping.remove(parse(socketChannel));
                log.warn("IP:{}被移除!!!", parse(socketChannel));
                List<String> keys = Splitter.on(":").splitToList(uniqueName);
                taskRegistryService.deleteByNotActive(keys.get(0), keys.get(1), parse(socketChannel.remoteAddress()));
                return true;
            }
            return false;
        });
        activeChannel.values().removeAll(collection);
    }

    @PreDestroy
    public void stop() {
        ThreadPoolRegisterCenter.destroy(heartRespExecutor, saveDelayTaskExecutor, saveRegExecutor);
    }

}
