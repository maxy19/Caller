package com.maxy.caller.remoting.server.netty.spmc;

import io.netty.channel.Channel;
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

  private Channel channel;

}
