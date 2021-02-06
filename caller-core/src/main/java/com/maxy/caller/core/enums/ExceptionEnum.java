package com.maxy.caller.core.enums;

/**
 * @author maxy
 */
public enum ExceptionEnum {

    BIZ_ERROR(1000, "业务端方法出现异常"),
    SYS_ERROR(2000, "系统错误"),
    PARAM_ERROR(3000, "参数错误."),
    DB_ERROR(4000, "数据库错误."),
    ;

    private int code;
    private String message;

    ExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
