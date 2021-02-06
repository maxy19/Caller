package com.maxy.caller.core.enums;

/**
 * @Author maxy
 **/
public enum ExecutionStatusEnum {
    /**
     * 0:未上线 1:已上线 2.暂停 3.准备阶段 ,4:执行中 5:重试中 6:执行成功 7:执行失败 8 已超期
     */
    NO_START((byte) 0, "未上线"),
    ONLINE((byte) 1, "已上线"),
    PARSE((byte) 2, "暂停"),
    READY((byte) 3, "准备阶段"),
    RUNNING((byte) 4, "执行中"),
    RETRYING((byte) 5, "重试中."),
    EXECUTION_SUCCEED((byte) 6, "执行成功."),
    EXECUTION_FAILED((byte) 7, "执行失败."),
    EXPIRED ((byte) 8, "已超期.");

    private Byte code;
    private String desc;

    ExecutionStatusEnum(Byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public Byte getCode() {
        return code;
    }
}
