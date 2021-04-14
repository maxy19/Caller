package com.maxy.caller.core.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author maxy
 */
@Component
@Data
public class GeneralConfigCenter {

    @Value("${caller.admin.limit.num:200}")
    private String limitNum;

    @Value("${caller.admin.pre.read:500}")
    private String preReadLimit;

    @Value("${caller.admin.push.cycle:900}")
    private Integer pushCycleTime;

    @Value("${caller.admin.max.slot:256}")
    private Integer totalSlot;
}
