package com.maxy.caller.remoting.server.netty.spmc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author maxuyang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event<T> {

  private T element;

  private String address;

}
