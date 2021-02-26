package com.maxy.caller.admin.config;

import com.google.common.collect.ImmutableList;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author maxy
 */
@Component
@Data
public class AdminConfigCenter {

    @Value("${caller.admin.limit.num:500}")
    private String limitNum;

    @Value("${caller.admin.limit.num:1000}")
    private String indexLimitNum;

    @Value("${caller.admin.pre.read:100}")
    private String preReadLimit;

    /**
     * hashTag
     * value:0,slot:13907
     * value:2,slot:5649
     * value:3,slot:1584
     */
    @Value("#{'${caller.admin.hash.tag:0,2,3}'.split(',')}")
    private List<Integer> tags = ImmutableList.of();
}
