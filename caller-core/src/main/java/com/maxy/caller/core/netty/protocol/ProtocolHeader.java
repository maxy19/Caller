package com.maxy.caller.core.netty.protocol;


import com.maxy.caller.common.utils.JSONUtils;
import com.maxy.caller.core.enums.MsgTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;

import static com.maxy.caller.core.enums.SerializationTypeEnum.KRYO;

/**
 * 说明：协议消息头
 *
 * @author maxy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProtocolHeader {

    private byte magic;    // 魔数   1
    private byte msgType;    // 消息类型 1
    private short reserve;    // 保留字 2
    private short serialization;  // 序列号 2
    private int length;        // 长度 4

    public static ProtocolHeader toEntity(Object transform, MsgTypeEnum msgType) {
        return create(transform.toString().getBytes(StandardCharsets.UTF_8).length, msgType);
    }

    private static ProtocolHeader create(int length, MsgTypeEnum msgType) {
        ProtocolHeader protocolHeader = new ProtocolHeader();
        protocolHeader.setMagic((byte) 0x01);
        protocolHeader.setMsgType(msgType.getCode());
        protocolHeader.setReserve((short) 0);
        protocolHeader.setSerialization(KRYO.getCode());
        protocolHeader.setLength(length);
        return protocolHeader;
    }

    @Override
    public String toString() {
        return JSONUtils.toJSONString(this);
    }


}
