package com.maxy.caller.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.maxy.caller.pojo.Pagination;
import lombok.Data;

/**
 * @author maxy
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class QueryConditionVO {
    /**
     * 查询条件1
     */
    private String groupKey;
    /**
     * 查询条件2
     */
    private String bizKey;
    /**
     * 分页
     */
    private Pagination pagination;

}
