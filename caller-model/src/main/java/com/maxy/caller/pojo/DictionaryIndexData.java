package com.maxy.caller.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DictionaryIndexData {
    /**
     * group+biz+topic
     */
    private String key;
    /**
     * 执行时间
     */
    private Long time;
}
