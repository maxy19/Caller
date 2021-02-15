package com.maxy.caller.core.enums;

/**
 * @Author maxy
 **/
public enum AddressTypeEnum {

    AUTO_UPLOAD((byte) 1, "自动上传"),
    MANUAL_UPLOAD((byte) 2, "手动上传");

    private Byte code;
    private String desc;

    AddressTypeEnum(Byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Byte getCode() {
        return code;
    }
}
