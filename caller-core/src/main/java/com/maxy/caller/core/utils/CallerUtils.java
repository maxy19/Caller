package com.maxy.caller.core.utils;

import com.google.common.collect.TreeMultimap;
import com.maxy.caller.bo.TaskDetailInfoBO;
import com.maxy.caller.pojo.DictionaryIndexData;
import io.netty.channel.Channel;

import java.net.SocketAddress;
import java.util.List;
import java.util.NavigableSet;
import java.util.stream.Collectors;

/**
 * @author maxuyang
 */
public class CallerUtils {

    private CallerUtils() {
    }

    public static String parse(final Channel channel) {
        if (null == channel) {
            return "";
        }
        SocketAddress remote = channel.remoteAddress();
        final String addr = remote != null ? remote.toString() : "";
        if (addr.length() > 0) {
            int index = addr.lastIndexOf("/");
            if (index >= 0) {
                return addr.substring(index + 1);
            }
            return addr;
        }
        return "";
    }

    public static List<String> parse(final List<Channel> channels) {
        return channels.stream().map(CallerUtils::parse).collect(Collectors.toList());
    }

    public static String parse(SocketAddress socketAddress) {
        if (socketAddress != null) {
            final String addr = socketAddress.toString();

            if (addr.length() > 0) {
                return addr.substring(1);
            }
        }
        return "";
    }

    public static List<DictionaryIndexData> filter(List<TaskDetailInfoBO> taskDetailInfoBOList) {
        TreeMultimap<String, Long> map = TreeMultimap.create();
        taskDetailInfoBOList.forEach(ele -> {
            map.put(String.join(":", ele.getGroupKey(), ele.getBizKey(), ele.getTopic()), ele.getExecutionTime().getTime());
        });
        //group+biz+topic 10:00
        //group+biz+topic 11:00
        //group+biz+topic 12:00
        return map.keySet().stream().map(key -> {
            NavigableSet<Long> executionTimeSet = map.get(key);
            return DictionaryIndexData.builder().key(key).time(executionTimeSet.last()).build();
        }).collect(Collectors.toList());
    }


}
