package com.maxy.caller.dto;

import com.maxy.caller.common.utils.JSONUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author maxy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultDTO<T> {
    /**
     * 状态编码
     */
    private Integer code;
    /**
     * 返回消息
     */
    private String message;
    /**
     * 返回对象
     */
    private T data;
    /**
     * 是否业务成功
     */
    private Boolean success;

    public ResultDTO(int code, Boolean success) {
        this.code = code;
        this.success = success;
    }

    public static <T> ResultDTO<T> getSuccessResult(T data) {
        ResultDTO<T> resultDTO = getSuccessResponse();
        resultDTO.setData(data);
        resultDTO.setCode(0);
        resultDTO.setSuccess(true);
        return resultDTO;
    }

    public static <T> ResultDTO<T> getErrorResult(Integer statusCode, String message) {
        ResultDTO<T> resultDTO = getSuccessResponse();
        resultDTO.setCode(statusCode);
        resultDTO.setMessage(message);
        resultDTO.setSuccess(false);
        return resultDTO;
    }

    public static <T> ResultDTO<T> getSuccessResponse() {
        return new ResultDTO<>(0, true);
    }

    /**
     * 消费端判断是否成功
     *
     * @return
     */
    public Boolean isSuccess() {
        return getSuccess();
    }

    @Override
    public String toString() {
        return JSONUtils.toJSONString(this);
    }
}
