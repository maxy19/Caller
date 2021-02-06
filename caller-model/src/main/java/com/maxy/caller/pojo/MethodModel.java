package com.maxy.caller.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * @author maxy
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MethodModel {

    private Object target;
    private Method method;

}
