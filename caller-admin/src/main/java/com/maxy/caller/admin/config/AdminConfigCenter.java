package com.maxy.caller.admin.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author maxuyang
 */
@Component
@Data
public class AdminConfigCenter {

    @Value("${caller.admin.limit.num:1000}")
    private String limitNum;

    @Value("${caller.admin.limit.num:1000}")
    private String indexLimitNum;

}
