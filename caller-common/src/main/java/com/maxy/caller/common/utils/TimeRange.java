package com.maxy.caller.common.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @author maxy
 */
@Data
@AllArgsConstructor
public class TimeRange {

	private Date start;

	private Date end;

	public TimeRange() { }

}
