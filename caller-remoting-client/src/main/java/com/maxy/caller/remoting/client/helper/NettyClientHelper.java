package com.maxy.caller.remoting.client.helper;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.maxy.caller.core.config.DispatchCenter;
import com.maxy.caller.core.config.ThreadPoolConfig;
import com.maxy.caller.core.config.ThreadPoolRegisterCenter;
import com.maxy.caller.core.enums.MsgTypeEnum;
import com.maxy.caller.core.netty.protocol.ProtocolMsg;
import com.maxy.caller.dto.CallerTaskDTO;
import com.maxy.caller.dto.ResultDTO;
import com.maxy.caller.dto.RpcRequestDTO;
import com.maxy.caller.pojo.MethodModel;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.maxy.caller.core.constant.ThreadConstant.CLIENT_INVOKE_METHOD_THREAD_POOL;
import static com.maxy.caller.core.enums.ExceptionEnum.BIZ_ERROR;
import static com.maxy.caller.core.utils.CallerUtils.parse;

/**
 * @author maxy
 */
@Log4j2
@Data
@Component
public class NettyClientHelper {

    private Channel channel;
    /**
     * 各种事件处理器
     */
    private Map<MsgTypeEnum, BiFunction<ProtocolMsg, Channel, Object>> eventMap = new ConcurrentHashMap();
    private ThreadPoolConfig threadPoolConfig = ThreadPoolConfig.getInstance();
    private ExecutorService clientInvokeMethodExecutor = threadPoolConfig.getPublicThreadPoolExecutor(true, CLIENT_INVOKE_METHOD_THREAD_POOL);

    /**
     * 服务端客户端共有事件
     */ {
        eventMap.put(MsgTypeEnum.MESSAGE, (protocolMsg, channel) -> {
            String msg = (String) getRequest(protocolMsg);
            log.info("消息:{},远程地址:{}", msg, parse(channel));
            return this;
        });
    }

    /**
     * 客户端解析服务端
     */
    public Supplier<NettyClientHelper> callerTaskEvent = () -> {
        eventMap.put(MsgTypeEnum.EXECUTE, (protocolMsg, channel) -> {
            clientInvokeMethodExecutor.execute(() -> {
                Stopwatch stopwatch = Stopwatch.createStarted();
                RpcRequestDTO request = (RpcRequestDTO) getRequest(protocolMsg);
                CallerTaskDTO callerTaskDTO = request.getCallerTaskDTO();
                log.info("callerTaskEvent#任务ID:{}", callerTaskDTO.getDetailTaskId());
                MethodModel methodModel = DispatchCenter.get(callerTaskDTO.getUniqueKey());
                Method method = methodModel.getMethod();
                method.setAccessible(true);
                Object result = null;
                try {
                    result = method.invoke(methodModel.getTarget(), callerTaskDTO.getExecutionParam());
                    //发送出去
                    channel.writeAndFlush(ProtocolMsg.toEntity((ResultDTO) result, callerTaskDTO, protocolMsg.getRequestId()));
                } catch (Exception e) {
                    if (Objects.isNull(result)) {
                        channel.writeAndFlush(ProtocolMsg.toEntity(ResultDTO.getErrorResult(BIZ_ERROR.getCode(), e.getMessage()),
                                callerTaskDTO,
                                protocolMsg.getRequestId()));
                    }
                    log.error("执行方法:{}|参数:{}.出现异常!", methodModel.getTarget(), callerTaskDTO.getExecutionParam(), e);
                }
                log.info("callerTaskEvent#耗时:{}", stopwatch.elapsed(TimeUnit.MILLISECONDS));
            });
            return this;
        });
        return this;
    };


    private Object getRequest(ProtocolMsg protocolMsg) {
        Object request = protocolMsg.getBody();
        Preconditions.checkArgument(Objects.nonNull(request));
        return request;
    }

    @PreDestroy
    public void stop() {
        ThreadPoolRegisterCenter.destroy(clientInvokeMethodExecutor);
    }

}
