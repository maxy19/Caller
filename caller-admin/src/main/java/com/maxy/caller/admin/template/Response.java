package com.maxy.caller.admin.template;


import com.maxy.caller.core.enums.ExceptionEnum;
import com.maxy.caller.core.exception.BusinessException;
import lombok.Data;

import java.io.Serializable;

/**
 * @author maxy
 */
@Data
public class Response<T> implements Serializable {

    private int code;

    private String message;

    private T data;

    private boolean success = false;

    private Response() {
    }

    public Response(int code, String message, boolean success) {
        this.code = code;
        this.message = message;
        this.success = success;
    }

    public Response(ExceptionEnum e) {
        this.code = e.getCode();
        this.message = e.getMessage();
    }

    public Response(BusinessException e) {
        this.code = e.getErrorCode();
        this.message = e.getErrorMsg();
    }

    public static <T> Response<T> getSuccessResponse() {
        return new Response<>(0, "", true);
    }

    public static <T> Response<T> getSuccessResponse(T data) {
        Response<T> response = getSuccessResponse();
        response.setData(data);
        response.setCode(0);
        response.setSuccess(true);
        return response;
    }

    public static <T> Response<T> error(ExceptionEnum e) {
        return new Response(e);
    }

    public static <T> Response<T> error(int code, String message) {
        return new Response(code, message, false);
    }

    public static <T> Response<T> error(BusinessException e) {
        return new Response(e);
    }

    public boolean isSuccess() {
        return success;
    }
}
