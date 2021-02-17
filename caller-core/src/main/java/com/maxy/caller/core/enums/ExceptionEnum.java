package com.maxy.caller.core.enums;

/**
 * @author maxy
 */
public enum ExceptionEnum {

    BIZ_ERROR(1000, "业务端方法出现异常"),
    FOUND_NOT_EXECUTE_INFO(1001, "没有找到当前执行信息."),
    FOUND_NOT_CURRENT_BASE_INFO(1002, "没有找到当前基础信息."),
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
