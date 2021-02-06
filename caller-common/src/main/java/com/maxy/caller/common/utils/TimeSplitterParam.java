package com.maxy.caller.common.utils;


import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Calendar;
import java.util.Map;

/**
 * 时间分割参数
 * @author maxy
 */
@Data
public class TimeSplitterParam {
	/**
	 * 默认分割方式
	 */
	public static final String DEFAULT_TYPE = "s";
	/**
	 * 默认分割数值
	 */
	public static final int DEFAULT_NUMBER = 1;
	
	private static final Map<String, Integer> types = Maps.newConcurrentMap();
	static {
		types.put("d", Calendar.DAY_OF_YEAR);
		types.put("h", Calendar.HOUR);
		types.put("m", Calendar.MINUTE);
		types.put("s", Calendar.SECOND);
	}
	
	public TimeSplitterParam() {}
	/**
	 * Creates a new instance of TimeSpliterParam.
	 *
	 * @param type
	 * @param number
	 */
	public TimeSplitterParam(int type, int number) {
		if (number == 0) {
			throw new IllegalArgumentException("split number must be big than zero! param number is:" + number);
		}
		this.type = type;
		this.number = number;
	}
	
	/**
	 * 创建时间分割参数
	 * @param number
	 * @return
	 */
	public static TimeSplitterParam newTimeSplitterParam(int number) {
		return new TimeSplitterParam(types.get(DEFAULT_TYPE), number);
	}
	
	/**
	 * 创建时间分割参数
	 * @param type
	 * @param number
	 * @return
	 */
	public static TimeSplitterParam newTimeSplitterParam(String type, int number) {
		return new TimeSplitterParam(types.get(type.toLowerCase()), number);
	}
	
	/**
	 * 类型
	 */
	private int type;
	
	/**
	 * 数值
	 */
	private int number;


}
