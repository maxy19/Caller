package com.maxy.caller.admin.template;


import com.maxy.caller.core.enums.ExceptionEnum;
import com.maxy.caller.core.exception.BusinessException;
import lombok.extern.log4j.Log4j2;

/**通用门面模板
 * @author maxy
 */
@Log4j2
public class FacadeTemplate {

	/**
	 * 通用门面模板，将方法日志打印,参数验证 到 核心业务执行
	 * @param executeLog 执行log相关方法
	 * @param validator 校验接口
	 * @param bizCallback 回调业务
	 * @param <T> 返回参数泛型
	 * @param <U> 请求参数类型
	 * @return
	 */
    public static <T, U> Response<T> execute(ExecuteLog<U> executeLog,
                                             ParamValidator validator,
                                             BizCallback<T> bizCallback) {
        Response<T> response = null;
        try {
            long start = System.currentTimeMillis();
            executeLog.startLog();
			validator.validate();
            response = bizCallback.execute();
            executeLog.endLog(response, System.currentTimeMillis() - start);
        } catch (BusinessException e) {
            executeLog.printLog(e);
            response = Response.error(e);
        } catch (Exception e) {
            executeLog.printLog(e);
            response = Response.error(ExceptionEnum.SYS_ERROR.getCode(), e.getMessage());
        }
        return response;
    }

}
