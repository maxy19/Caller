package com.maxy.caller.admin.enums;

/**
 * @Author maxuyang
 **/
public enum AlarmStatusEnum {

    DEFAULT((byte) 0, "默认"),
    LOCK((byte) -1, "锁定"),
    NO_ALARM((byte) 1, "不用告警"),
    ALARM_SUCCESS((byte) 2, "告警成功"),
    ALARM_FAIL((byte) 3, "告警失败");

    private byte code;
    private String desc;

    AlarmStatusEnum(byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public byte getCode() {
        return code;
    }

    public void setCode(byte code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
