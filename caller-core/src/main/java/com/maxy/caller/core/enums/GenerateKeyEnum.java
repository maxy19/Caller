package com.maxy.caller.core.enums;


import java.util.Objects;

/**
 * @Author maxy
 **/
public enum GenerateKeyEnum {

    DETAIL_TASK_INFO("caller:detail:task:info:%s"),
    REPEAT_TASK_INFO("caller:repeat:%s:%s"),
    ZSET_QUEUE_FORMAT("caller:zset:{%s}"),
    LIST_QUEUE_FORMAT_BACKUP("caller:list:backup:{%s}"),
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
