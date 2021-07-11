package com.maxy.caller.core.netty.protocol;


import com.maxy.caller.common.utils.JSONUtils;
import com.maxy.caller.core.enums.MsgTypeEnum;
import com.maxy.caller.core.netty.pojo.Pinger;
import com.maxy.caller.dto.CallerTaskDTO;
import com.maxy.caller.dto.ResultDTO;
import com.maxy.caller.dto.RpcRequestDTO;
import com.maxy.caller.pojo.DelayTask;
import com.maxy.caller.pojo.RegConfigInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.maxy.caller.core.utils.CallerUtils.newId;


/**
 * 说明：消息对象
 *
 * @author maxy
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMsg<T> {

    private ProtocolHeader protocolHeader;
    private Long requestId;
    private MsgTypeEnum msgTypeEnum;
    private T body;

    public ProtocolMsg(MsgTypeEnum msgTypeEnum) {
        this.msgTypeEnum = msgTypeEnum;
    }

    /**
     * 初始化发送注册信息
     *
     * @return
     */
    public static ProtocolMsg toEntity(RegConfigInfo regConfigInfo) {
        ProtocolMsg<RpcRequestDTO> protocolMsg = new ProtocolMsg<>(MsgTypeEnum.REGISTRY);
        //set regConfigInfo
        RpcRequestDTO rpcRequestDTO = new RpcRequestDTO();
        rpcRequestDTO.setRegConfigInfo(regConfigInfo);
        //set header
        protocolMsg.setProtocolHeader(ProtocolHeader.toEntity(rpcRequestDTO, MsgTypeEnum.REGISTRY));
        //set body
        protocolMsg.setBody(rpcRequestDTO);
        //reqId
        protocolMsg.setRequestId(newId());
        return protocolMsg;
    }

    /**
     * 构建消息
     *
     * @param body
     * @return
     */
    public static ProtocolMsg toEntity(String body) {
        ProtocolMsg<String> protocolMsg = new ProtocolMsg<>(MsgTypeEnum.MESSAGE);
        //set header
        protocolMsg.setProtocolHeader(ProtocolHeader.toEntity(body, MsgTypeEnum.MESSAGE));
        //set body
        protocolMsg.setBody(body);
        //reqId
        protocolMsg.setRequestId(newId());
        return protocolMsg;
    }

    /**
     * 构建pinger
     *
     * @return
     */
    public static ProtocolMsg toEntity(Long timeMillis, String uniqName) {
        ProtocolMsg<Pinger> protocolMsg = new ProtocolMsg<>(MsgTypeEnum.HEARTBEAT);
        Pinger pinger = Pinger.builder().requestTime(timeMillis).uniqueName(uniqName).build();
        //set header
        protocolMsg.setProtocolHeader(ProtocolHeader.toEntity(pinger, MsgTypeEnum.HEARTBEAT));
        //set body
        protocolMsg.setBody(pinger);
        //reqId
        protocolMsg.setRequestId(newId());
        return protocolMsg;
    }

    /**
     * 构建结果
     *
     * @return
     */
    public static ProtocolMsg toEntity(ResultDTO resultDTO, CallerTaskDTO callerTaskDTO, Long requestId) {
        ProtocolMsg<RpcRequestDTO> protocolMsg = new ProtocolMsg<>(MsgTypeEnum.RESULT);
        //set resultDTO
        RpcRequestDTO rpcRequestDTO = new RpcRequestDTO();
        rpcRequestDTO.setResultDTO(resultDTO);
        rpcRequestDTO.setCallerTaskDTO(callerTaskDTO);
        //set header
        protocolMsg.setProtocolHeader(ProtocolHeader.toEntity(rpcRequestDTO, MsgTypeEnum.RESULT));
        //set body
        protocolMsg.setBody(rpcRequestDTO);
        //reqId
        protocolMsg.setRequestId(requestId);
        return protocolMsg;
    }

    /**
     * 构建结果
     *
     * @return
     */
    public static ProtocolMsg toEntity(CallerTaskDTO task) {
        ProtocolMsg<RpcRequestDTO> protocolMsg = new ProtocolMsg<>(MsgTypeEnum.EXECUTE);
        //set regConfigInfo
        RpcRequestDTO rpcRequestDTO = new RpcRequestDTO();
        rpcRequestDTO.setCallerTaskDTO(task);
        //set header
        protocolMsg.setProtocolHeader(ProtocolHeader.toEntity(rpcRequestDTO, MsgTypeEnum.EXECUTE));
        //set body
        protocolMsg.setBody(rpcRequestDTO);
        //reqId
        protocolMsg.setRequestId(newId());
        return protocolMsg;
    }

    /**
     * 发送topic
     *
     * @return
     */
    public static ProtocolMsg toEntity(List<DelayTask> delayTasks) {
        ProtocolMsg<RpcRequestDTO> protocolMsg = new ProtocolMsg<>(MsgTypeEnum.DELAYTASK);
        //set regConfigInfo
        RpcRequestDTO rpcRequestDTO = new RpcRequestDTO();
        rpcRequestDTO.setDelayTasks(delayTasks);
        //set header
        protocolMsg.setProtocolHeader(ProtocolHeader.toEntity(rpcRequestDTO, MsgTypeEnum.DELAYTASK));
        //set body
        protocolMsg.setBody(rpcRequestDTO);
        //reqId
        protocolMsg.setRequestId(newId());
        return protocolMsg;
    }

    @Override
    public String toString() {
        return JSONUtils.toJSONString(this);
    }
}
