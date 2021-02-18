package com.maxy.caller.core.enums;

/**
 * @Author maxy
 **/
public enum MsgTypeEnum {
    /**
     * 心跳事件
     */
    PINGER((byte) 1),
    /**
     * 消息事件
     */
    MESSAGE((byte) 2),
    /**
     * 响应事件
     */
    RESULT((byte) 3),
    /**
     * 注册事件
     */
    REGISTRY((byte) 4),
    /**
     * 发送topic保存事件
     */
    DELAYTASK((byte) 5),
    /**
     * 执行事件
     */
    EXECUTE((byte) 6);

    private byte code;

    MsgTypeEnum(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }
}
