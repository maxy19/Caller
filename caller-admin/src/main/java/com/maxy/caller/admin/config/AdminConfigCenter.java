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

    @Value("${caller.admin.limit.num:5000}")
    private String limitNum;

    @Value("${caller.admin.limit.num:10}")
    private String indexLimitNum;

    @Value("${caller.admin.pre.read:1000}")
    private String preReadLimit;

    @Value("${caller.admin.push.cycle:60}")
    private Integer pushCycleTime;
    @Value("${caller.admin.pop.cycle:30}")
    private Integer popCycleTime;

    /**
     * hashTag
     * value:0,slot:13907
     * value:2,slot:5649
     * value:3,slot:1584
     */
    @Value("#{'${caller.admin.hash.tag:0,2,3}'.split(',')}")
    private List<Integer> tags = ImmutableList.of();
}
