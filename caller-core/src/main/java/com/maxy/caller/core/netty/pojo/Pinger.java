package com.maxy.caller.core.netty.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author maxy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pinger {

    private String uniqueName;
    private Long requestTime;

}
