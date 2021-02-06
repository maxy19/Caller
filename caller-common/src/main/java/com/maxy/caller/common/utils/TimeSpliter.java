package com.maxy.caller.common.utils;

import com.google.common.collect.Lists;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 时间分割工具类
 */
public class TimeSpliter {
    private TimeSpliter() {
    }

    private static final int SPLIT_HOURS = 24;
    private static final int ADVNCE_TIME_MIN = 2;

    /**
     * 生成AccountTask需要的时间分割计划
     *
     * @param
     * @return
     */
    public static List<TimeRange> generateTimeUnitForAccountTask(TimeRange timeRange, TimeSplitterParam splitParam) {
        if (timeRange == null ||
                timeRange.getStart().compareTo(timeRange.getEnd()) > 0) {
            return Collections.emptyList();
        }
        if (timeRange.getStart().equals(timeRange.getEnd())) {    //如果开始时间=结束时间,将开始时间前推24小时
            timeRange.setStart(DateUtils.addHours(timeRange.getStart(), -SPLIT_HOURS));
        }

        List<TimeRange> list = generateTimeUnit(timeRange.getStart(), timeRange.getEnd(), splitParam);
        TimeRange lastTm = list.get(list.size() - 1);    //取出最后一小时将将分钟提前2分钟
        lastTm.setEnd(DateUtils.addMinutes(lastTm.getEnd(), -ADVNCE_TIME_MIN));
        return list;
    }

    /**
     * 将一段时间切分成若干段时间
     *
     * @param timeRange 时间段
     * @param splitHour 按几个小时为一段切分
     * @return
     */
    public static List<TimeRange> generateTimeUnit(TimeRange timeRange, int splitHour) {
        if (timeRange == null) {
            return Collections.emptyList();
        }
        return generateTimeUnit(timeRange.getStart(), timeRange.getEnd(), TimeSplitterParam.newTimeSplitterParam(splitHour));
    }

    /**
     * 按开始时间,对事时间和分割规则将时间分割
     *
     * @param start
     * @param end
     * @param splitParam ${TimeSpliterParam }
     * @return
     */
    public static List<TimeRange> generateTimeUnit(Date start, Date end, TimeSplitterParam splitParam) {
        if (!start.before(end)) {
            return Collections.emptyList();
        }

        List<TimeRange> units = Lists.newArrayList();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date nextTime = start;
        while (nextTime.before(end)) {
            nextTime = getNextDate(start, splitParam);
            if (nextTime.after(end)) {
                nextTime = end;
            }
            if (!Objects.equals(simpleDateFormat.format(start), simpleDateFormat.format(nextTime))) {
                units.add(new TimeRange(start, nextTime));
            }
            start = nextTime;
        }
        return units;
    }

    /**
     * 得到下一下日期
     *
     * @param date
     * @param spliterParam
     * @return
     */
    private static Date getNextDate(Date date, TimeSplitterParam spliterParam) {
        Calendar clr = Calendar.getInstance();
        clr.setTime(date);
        clr.add(spliterParam.getType(), spliterParam.getNumber());
        return clr.getTime();
    }
}
