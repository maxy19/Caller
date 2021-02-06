package com.maxy.caller.core.enums;

/**
 * @Author maxuyang
 **/
public enum GroupStatusEnum {

    UN_LOCK((byte) 0, "未加锁"),
    LOCK((byte) 1, "加锁");

    private Byte code;
    private String desc;

    GroupStatusEnum(Byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Byte getCode() {
        return code;
    }
}
