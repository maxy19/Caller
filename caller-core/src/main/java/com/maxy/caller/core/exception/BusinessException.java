package com.maxy.caller.core.exception;

import com.maxy.caller.core.enums.ExceptionEnum;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private int errorCode;

    private String errorMsg;

    public BusinessException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorMsg = message;
    }

    public BusinessException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorMsg = message;
    }

    public BusinessException(ExceptionEnum e) {
        super(e.getMessage());
        this.errorCode = e.getCode();
        this.errorMsg = e.getMessage();
    }


}
