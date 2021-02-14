package com.maxy.caller.core.netty.protocol;


import com.maxy.caller.common.utils.JSONUtils;
import com.maxy.caller.core.enums.EventEnum;
import com.maxy.caller.core.netty.pojo.Pinger;
import com.maxy.caller.dto.CallerTaskDTO;
import com.maxy.caller.dto.ResultDTO;
import com.maxy.caller.dto.RpcRequestDTO;
import com.maxy.caller.pojo.DelayTask;
import com.maxy.caller.pojo.RegConfigInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

import static com.maxy.caller.core.utils.CallerUtils.getReqId;


/**
 * 说明：消息对象
 *
 * @author maxy
 */
@Data
@Builder
@AllArgsConstructor
public class ProtocolMsg<T> {

    private ProtocolHeader protocolHeader;
    private String requestId = getReqId();
    private EventEnum eventEnum;
    private T body;

    public ProtocolMsg(EventEnum eventEnum) {
        this.eventEnum = eventEnum;
    }

    private ProtocolMsg() {
    }

    /**
     * 初始化发送注册信息
     *
     * @return
     */
    public static ProtocolMsg toEntity(RegConfigInfo regConfigInfo) {
        ProtocolMsg<RpcRequestDTO> protocolMsg = new ProtocolMsg<>(EventEnum.REGISTRY);
        //set regConfigInfo
        RpcRequestDTO rpcRequestDTO = new RpcRequestDTO();
        rpcRequestDTO.setRegConfigInfo(regConfigInfo);
        //set header
        protocolMsg.setProtocolHeader(ProtocolHeader.toEntity(rpcRequestDTO));
        //set body
        protocolMsg.setBody(rpcRequestDTO);
        return protocolMsg;
    }

    /**
     * 构建消息
     *
     * @param body
     * @return
     */
    public static ProtocolMsg toEntity(String body) {
        ProtocolMsg<String> protocolMsg = new ProtocolMsg<>(EventEnum.MESSAGE);
        //set header
        protocolMsg.setProtocolHeader(ProtocolHeader.toEntity(body));
        //set body
        protocolMsg.setBody(body);
        return protocolMsg;
    }

    /**
     * 构建pinger
     *
     * @return
     */
    public static ProtocolMsg toEntity(Long timeMillis, String uniqName) {
        ProtocolMsg<Pinger> protocolMsg = new ProtocolMsg<>(EventEnum.PINGER);
        Pinger pinger = Pinger.builder().requestTime(timeMillis).uniqueName(uniqName).build();
        //set header
        protocolMsg.setProtocolHeader(ProtocolHeader.toEntity(pinger));
        //set body
        protocolMsg.setBody(pinger);
        return protocolMsg;
    }

    /**
     * 构建结果
     *
     * @return
     */
    public static ProtocolMsg toEntity(ResultDTO resultDTO, CallerTaskDTO callerTaskDTO) {
        ProtocolMsg<RpcRequestDTO> protocolMsg = new ProtocolMsg<>(EventEnum.RESULT);
        //set resultDTO
        RpcRequestDTO rpcRequestDTO = new RpcRequestDTO();
        rpcRequestDTO.setResultDTO(resultDTO);
        rpcRequestDTO.setCallerTaskDTO(callerTaskDTO);
        //set header
        protocolMsg.setProtocolHeader(ProtocolHeader.toEntity(rpcRequestDTO));
        //set body
        protocolMsg.setBody(rpcRequestDTO);
        return protocolMsg;
    }

    /**
     * 构建结果
     *
     * @return
     */
    public static ProtocolMsg toEntity(CallerTaskDTO task) {
        ProtocolMsg<RpcRequestDTO> protocolMsg = new ProtocolMsg<>(EventEnum.EXECUTE);
        //set regConfigInfo
        RpcRequestDTO rpcRequestDTO = new RpcRequestDTO();
        rpcRequestDTO.setCallerTaskDTO(task);
        //set header
        protocolMsg.setProtocolHeader(ProtocolHeader.toEntity(rpcRequestDTO));
        //set body
        protocolMsg.setBody(rpcRequestDTO);
        return protocolMsg;
    }

    /**
     * 发送topic
     *
     * @return
     */
    public static ProtocolMsg toEntity(List<DelayTask> delayTasks) {
        ProtocolMsg<RpcRequestDTO> protocolMsg = new ProtocolMsg<>(EventEnum.DELAYTASK);
        //set regConfigInfo
        RpcRequestDTO rpcRequestDTO = new RpcRequestDTO();
        rpcRequestDTO.setDelayTasks(delayTasks);
        //set header
        protocolMsg.setProtocolHeader(ProtocolHeader.toEntity(rpcRequestDTO));
        //set body
        protocolMsg.setBody(rpcRequestDTO);
        return protocolMsg;
    }

    @Override
    public String toString() {
        return JSONUtils.toJSONString(this);
    }
}
