package com.maxy.caller.remoting.server.netty.helper;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.maxy.caller.bo.TaskDetailInfoBO;
import com.maxy.caller.bo.TaskRegistryBO;
import com.maxy.caller.common.utils.JSONUtils;
import com.maxy.caller.core.config.ThreadPoolConfig;
import com.maxy.caller.core.config.ThreadPoolRegisterCenter;
import com.maxy.caller.core.enums.MsgTypeEnum;
import com.maxy.caller.core.exception.BusinessException;
import com.maxy.caller.core.netty.pojo.Pinger;
import com.maxy.caller.core.netty.protocol.ProtocolMsg;
import com.maxy.caller.core.service.Cache;
import com.maxy.caller.core.service.CommonService;
import com.maxy.caller.core.service.TaskDetailInfoService;
import com.maxy.caller.core.service.TaskLogService;
import com.maxy.caller.core.service.TaskRegistryService;
import com.maxy.caller.dto.CallerTaskDTO;
import com.maxy.caller.dto.ResultDTO;
import com.maxy.caller.dto.RpcRequestDTO;
import com.maxy.caller.pojo.RegConfigInfo;
import com.maxy.caller.remoting.server.netty.spmc.RingbufferInvoker;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
import static com.maxy.caller.core.constant.ThreadConstant.SERVER_SAVE_REG_THREAD_POOL;
import static com.maxy.caller.core.enums.ExceptionEnum.FOUND_NOT_EXECUTE_INFO;
import static com.maxy.caller.core.enums.ExecutionStatusEnum.EXECUTION_FAILED;
import static com.maxy.caller.core.enums.ExecutionStatusEnum.EXECUTION_SUCCEED;
import static com.maxy.caller.core.enums.GenerateKeyEnum.DETAIL_TASK_INFO;
import static com.maxy.caller.core.utils.CallerUtils.parse;

/**
 * @author maxy
 */
@Log4j2
@Data
@Component
public class NettyServerHelper implements CommonService {

    @Resource
    private TaskRegistryService taskRegistryService;
    @Resource
    private RingbufferInvoker ringbufferInvoker;
    @Resource
    private Cache cache;
    @Resource
    private TaskDetailInfoService taskDetailInfoService;
    @Resource
    private TaskLogService taskLogService;
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
    private ExecutorService saveRegExecutor = ThreadPoolConfig.getInstance().getSingleThreadExecutor(true, SERVER_SAVE_REG_THREAD_POOL);
    private ExecutorService heartRespExecutor = ThreadPoolConfig.getInstance().getSingleThreadExecutor(true, SERVER_HEART_RESP_THREAD_POOL);
    private ExecutorService respResultExecutor = ThreadPoolConfig.getInstance().getPublicThreadPoolExecutor(true, SERVER_RESP_RESULT_THREAD_POOL);

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
                log.info("heartbeatEvent#服务端收到客户端的心跳消息:{}", pinger);
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
                Byte newVal = REQUEST_MAP.computeIfPresent(protocolMsg.getRequestId(), (k, v) -> v == LOCK_DEFAULT ? LOCK_SUCCESS : v);
                if(Objects.equals(newVal, LOCK_SUCCESS)) {
                    log.info("resultEvent#收到客户端响应.请求Id:{}", protocolMsg.getRequestId());
                    getHandleCallback().accept(protocolMsg, channel);
                    //抢锁成功再删除
                    REQUEST_MAP.remove(protocolMsg.getRequestId());
                }
                log.info("resultEvent:reqId:{},耗时:{}", protocolMsg.getRequestId(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
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
            ringbufferInvoker.invoke(request.getDelayTasks(), channel);
            log.info("delayTaskEvent#接受客户端添加延迟任务数量:{}", request.getDelayTasks().size());
        });
        return this;
    };

    /**
     * 获取请求
     *
     * @param protocolMsg
     * @return
     */
    private Object getRequest(ProtocolMsg protocolMsg) {
        Object request = protocolMsg.getBody();
        Preconditions.checkArgument(Objects.nonNull(request));
        return request;
    }

    /**
     * 移除非活跃连接
     *
     * @param groupName
     */
    public void removeNotActive(String groupName) {
        //去掉不活跃的
        List<Channel> collection = Lists.newArrayList();
        List<Channel> channels = activeChannel.get(groupName);
        if (CollectionUtils.isEmpty(channels)) {
            log.warn("removeNotActive#{}没有找到channel信息,不进行移除非活跃用户操作.", groupName);
            return;
        }
        channels.removeIf(socketChannel -> {
            if (!socketChannel.isActive()) {
                collection.add(socketChannel);
                ipChannelMapping.remove(parse(socketChannel));
                log.warn("IP:{}被移除!!!", parse(socketChannel));
                List<String> keys = Splitter.on(":").splitToList(groupName);
                taskRegistryService.deleteByNotActive(keys.get(0), keys.get(1), parse(socketChannel.remoteAddress()));
                return true;
            }
            return false;
        });
        if (CollectionUtils.isNotEmpty(collection)) {
            activeChannel.values().removeAll(collection);
        }
    }


    /**
     * callback 处理
     */
    private BiConsumer<ProtocolMsg, Channel> handleCallback = ((response, channel) -> {
        Stopwatch stopwatch = Stopwatch.createStarted();
        RpcRequestDTO request = (RpcRequestDTO) getRequest(response);
        CallerTaskDTO dto = request.getCallerTaskDTO();
        TaskDetailInfoBO taskDetailInfoBO = getDetailTask(dto);
        if (Objects.isNull(taskDetailInfoBO)) {
            throw new BusinessException(FOUND_NOT_EXECUTE_INFO);
        }
        taskDetailInfoService.removeBackupCache(taskDetailInfoBO);
        ResultDTO resultDTO = request.getResultDTO();
        taskDetailInfoBO.setExecutionStatus(resultDTO.isSuccess() ? EXECUTION_SUCCEED.getCode() : EXECUTION_FAILED.getCode());
        taskDetailInfoBO.setErrorMsg(resultDTO.getMessage());
        taskDetailInfoService.update(taskDetailInfoBO);
        taskLogService.saveClientResult(taskDetailInfoBO, resultDTO.getMessage(), parse(channel));
        log.info("handleCallback#耗时:{}", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    });

    /**
     * 从缓存获取详情信息
     *
     * @param callerTaskDTO
     * @return
     */
    private TaskDetailInfoBO getDetailTask(CallerTaskDTO callerTaskDTO) {
        String value = cache.get(DETAIL_TASK_INFO.join(callerTaskDTO.getDetailTaskId()));
        if (StringUtils.isBlank(value)) {
            log.warn("getDetailTask#缓存中没有找到数据,从数据库中获取.");
            return taskDetailInfoService.getByInfoId(callerTaskDTO.getDetailTaskId());
        }
        return JSONUtils.parseObject(value, TaskDetailInfoBO.class);
    }


    @PreDestroy
    public void stop() {
        ThreadPoolRegisterCenter.destroy(heartRespExecutor, saveRegExecutor, respResultExecutor);
    }

}
