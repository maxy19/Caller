package com.maxy.caller.core.enums;


import java.util.Objects;

/**
 * @Author maxy
 **/
public enum GenerateKeyEnum {

    DICTIONARY_INDEX_FORMAT("caller:dic:index:{%s}"),
    DICTIONARY_INDEX_BACKUP_FORMAT("caller:dic:index:{%s}:backup"),
    INDEX_DATA_FORMAT("caller:%s:zset:{%s}"),
    ZSET_QUEUE_FORMAT("caller:%s:zset:{%s}"),
    ;

    private String key;

    GenerateKeyEnum(String key) {
        this.key = key;
    }

    public <T extends Object> String join(T... args) {
        if (Objects.isNull(args) || args.length == 0) {
            return null;
        }
        return String.format(key, args);
    }

    public String getKey() {
        return key;
    }

}
