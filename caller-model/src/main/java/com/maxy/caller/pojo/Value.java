package com.maxy.caller.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author maxy
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Value<T> {
    T value;
}
