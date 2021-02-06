package com.maxy.caller.admin.template;


/**
 * @author maxy
 */
public interface BizCallback<T> {

    Response<T> execute();
}
