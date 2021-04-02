package com.maxy.caller.core.mpmc;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author maxuyang
 */
@Data
@NoArgsConstructor
public class Event<T> {

  private T element;
}
