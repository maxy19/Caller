package com.maxy.caller.core.utils;

import com.google.common.base.Splitter;
import com.google.common.collect.TreeMultimap;
import com.maxy.caller.bo.TaskDetailInfoBO;
import com.maxy.caller.common.utils.IpUtils;
import com.maxy.caller.common.utils.MD5EncryptUtil;
import com.maxy.caller.pojo.DictionaryIndexData;
import io.netty.channel.Channel;
import org.apache.commons.collections.CollectionUtils;

import java.net.SocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.NavigableSet;
import java.util.stream.Collectors;

/**
 * @author maxy
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

    public static List<String> filterIp(List<String> addresses) {
        if (CollectionUtils.isNotEmpty(addresses)) {
            return addresses.stream().map(ele -> Splitter.on(":").splitToList(ele).get(0)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public static String getReqId() {
        return MD5EncryptUtil.encrypt(System.nanoTime() + IpUtils.getIp());
    }


}
