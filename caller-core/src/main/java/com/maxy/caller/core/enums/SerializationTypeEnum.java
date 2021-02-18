package com.maxy.caller.core.enums;

/**
 * @author maxuyang
 */
public enum SerializationTypeEnum {
    KRYO((short) 0x10),
    JSON((short) 0x20);

    private final short code;

    SerializationTypeEnum(short code) {
        this.code = code;
    }

    public short getCode() {
        return code;
    }
}