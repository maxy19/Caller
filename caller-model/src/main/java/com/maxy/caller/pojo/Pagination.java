package com.maxy.caller.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gaobo
 * @create 2020-10-05 17:12
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pagination {

    /**
     * 每页多少条
     */
    private Integer pageSize = 20;
    /**
     * 当前第几页
     */
    private Integer pageNum = 1;
    /**
     * 总条数
     */
    private Long totalCount;

}
