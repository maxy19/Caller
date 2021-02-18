package com.maxy.caller.remoting.server.netty.helper;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.maxy.caller.bo.TaskDetailInfoBO;
import com.maxy.caller.bo.TaskRegistryBO;
import com.maxy.caller.common.utils.BeanCopyUtils;
import com.maxy.caller.core.enums.MsgTypeEnum;
import com.maxy.caller.core.exception.BusinessException;
import com.maxy.caller.core.netty.pojo.Pinger;
import com.maxy.caller.core.netty.protocol.ProtocolMsg;
import com.maxy.caller.core.service.TaskDetailInfoService;
import com.maxy.caller.core.service.TaskGroupService;
import com.maxy.caller.core.service.TaskLogService;
import com.maxy.caller.core.service.TaskRegistryService;
import com.maxy.caller.dto.CallerTaskDTO;
import com.maxy.caller.dto.ResultDTO;
import com.maxy.caller.dto.RpcRequestDTO;
import com.maxy.caller.pojo.RegConfigInfo;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static com.maxy.caller.core.enums.ExceptionEnum.FOUND_NOT_EXECUTE_INFO;
import static com.maxy.caller.core.enums.ExecutionStatusEnum.EXECUTION_FAILED;
import static com.maxy.caller.core.enums.ExecutionStatusEnum.EXECUTION_SUCCEED;
import static com.maxy.caller.core.enums.ExecutionStatusEnum.ONLINE;
import static com.maxy.caller.core.utils.CallerUtils.parse;

/**
 * @author maxy
 */
@Log4j2
@Data
@Component
public class NettyServerHelper {

    @Resource
    private TaskDetailInfoService taskDetailInfoService;
    @Resource
    private TaskRegistryService taskRegistryService;
    @Resource
    private TaskLogService taskLogService;
    @Resource
    private TaskGroupService taskGroupService;
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

    /**
     * 服务端客户端共有事件
     */ {
        eventMap.put(MsgTypeEnum.MESSAGE, (protocolMsg, channel) -> {
            String msg = (String) getRequest(protocolMsg);
            log.info("服务端收到消息:{}", msg);
        });
    }

    /**
     * 服务端解析客户端事件
     */
    public Supplier<NettyServerHelper> registryEvent = () -> {
        eventMap.put(MsgTypeEnum.REGISTRY, (protocolMsg, channel) -> {
            RpcRequestDTO request = (RpcRequestDTO) getRequest(protocolMsg);
            RegConfigInfo regConfigInfo = request.getRegConfigInfo();
            log.info("registryEvent#接受客户端启动注册信息:{}", regConfigInfo);
            activeChannel.put(regConfigInfo.getUniqName(), channel);
            ipChannelMapping.put(parse(channel), channel);
            //保存register
            TaskRegistryBO taskRegistryBO = new TaskRegistryBO();
            BeanUtils.copyProperties(regConfigInfo, taskRegistryBO);
            taskRegistryBO.setRegistryAddress(parse(channel));
            taskRegistryService.save(taskRegistryBO);
            channel.writeAndFlush(ProtocolMsg.toEntity("服务端收到客户端:[" + parse(channel) + "]的注册信息！"));
        });
        return this;
    };

    /**
     * 服务端解析客户端事件
     */
    public Supplier<NettyServerHelper> pingerEvent = () -> {
        eventMap.put(MsgTypeEnum.PINGER, (protocolMsg, channel) -> {
            Pinger pinger = (Pinger) getRequest(protocolMsg);
            channel.writeAndFlush(ProtocolMsg.toEntity("服务端收到客户端的心跳消息!"));
            List<Channel> channels = activeChannel.get(pinger.getUniqueName());
            //去掉不活跃的
            channels.removeIf(element -> {
                if (!element.isActive()) {
                    removeNotActiveAddress(element);
                    List<String> keys = Splitter.on(":").splitToList(pinger.getUniqueName());
                    taskRegistryService.deleteByNotActive(keys.get(0), keys.get(1), parse(channel.remoteAddress()));
                    return true;
                }
                return false;
            });
        });
        return this;
    };

    public void removeNotActiveAddress(Channel channel) {
        activeChannel.values().remove(channel);
        ipChannelMapping.remove(parse(channel));
    }

    /**
     * 服务端解析客户端事件
     */
    public Supplier<NettyServerHelper> resultEvent = () -> {
        eventMap.put(MsgTypeEnum.RESULT, (protocolMsg, channel) -> {
            log.info("resultEvent#执行客户端方法返回值:{}", protocolMsg);
            RpcRequestDTO request = (RpcRequestDTO) getRequest(protocolMsg);
            if (Objects.isNull(request.getResultDTO())) {
                log.error("resultEvent#ResultDTO为空!参数:{}", protocolMsg);
                return;
            }
            CallerTaskDTO dto = request.getCallerTaskDTO();
            TaskDetailInfoBO taskDetailInfoBO = taskDetailInfoService.get(dto.getGroupKey(), dto.getBizKey(), dto.getTopic(), dto.getExecutionTime());
            if (Objects.isNull(taskDetailInfoBO)) {
                throw new BusinessException(FOUND_NOT_EXECUTE_INFO);
            }
            ResultDTO resultDTO = request.getResultDTO();
            taskDetailInfoBO.setExecutionStatus(resultDTO.isSuccess()
                    ? EXECUTION_SUCCEED.getCode() : EXECUTION_FAILED.getCode());
            taskDetailInfoBO.setErrorMsg(resultDTO.getMessage());
            taskDetailInfoService.update(taskDetailInfoBO);
            taskDetailInfoService.removeBackup(dto);
            taskLogService.saveClientResult(taskDetailInfoBO, resultDTO.getMessage(), parse(channel));
        });
        return this;
    };

    /**
     * 服务端解析客户端事件
     */
    public Supplier<NettyServerHelper> delayTaskEvent = () -> {
        eventMap.put(MsgTypeEnum.DELAYTASK, (protocolMsg, channel) -> {
            RpcRequestDTO request = (RpcRequestDTO) getRequest(protocolMsg);
            log.info("delayTaskEvent#接受客户端添加延迟任务:{}", request.getDelayTasks());
            List<TaskDetailInfoBO> taskDetailInfoBOList = BeanCopyUtils.copyListProperties(request.getDelayTasks(), TaskDetailInfoBO::new);
            taskDetailInfoService.batchInsert(taskDetailInfoBOList);
            taskLogService.batchInsert(taskDetailInfoBOList,ONLINE.getCode(),parse(channel));
        });
        return this;
    };

    private Object getRequest(ProtocolMsg protocolMsg) {
        Object request = protocolMsg.getBody();
        Preconditions.checkArgument(Objects.nonNull(request));
        return request;
    }

}
