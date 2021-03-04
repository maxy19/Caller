package com.maxy.caller.core.utils;

import com.maxy.caller.pojo.Value;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.ChannelProgressivePromise;
import lombok.extern.log4j.Log4j2;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author maxy
 */
@Log4j2
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

    public static boolean isChannelWritable(Channel channel) {
        if (Objects.isNull(channel)) {
            log.error("isChannelWritable#无法获取channel信息!!!");
            return false;
        }
        if (!channel.isWritable()) {
            log.error("isChannelWritable#channel:{}超过水位线无法发送服务端信息!!!", channel);
            return false;
        }
        return true;
    }

    public static List<String> parse(final List<Channel> channels) {
        return channels.stream().filter(Channel::isActive).map(CallerUtils::parse).collect(Collectors.toList());
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

    public static String getReqId() {
        return String.valueOf(System.nanoTime());
    }

    public static String getLocalAddress() {
        try {
            // Traversal Network interface to get the first non-loopback and non-private address
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            ArrayList<String> ipv4Result = new ArrayList<String>();
            ArrayList<String> ipv6Result = new ArrayList<String>();
            while (enumeration.hasMoreElements()) {
                final NetworkInterface networkInterface = enumeration.nextElement();
                final Enumeration<InetAddress> en = networkInterface.getInetAddresses();
                while (en.hasMoreElements()) {
                    final InetAddress address = en.nextElement();
                    if (!address.isLoopbackAddress()) {
                        if (address instanceof Inet6Address) {
                            ipv6Result.add(normalizeHostAddress(address));
                        } else {
                            ipv4Result.add(normalizeHostAddress(address));
                        }
                    }
                }
            }
            // prefer ipv4
            if (!ipv4Result.isEmpty()) {
                for (String ip : ipv4Result) {
                    if (ip.startsWith("127.0") || ip.startsWith("192.168")) {
                        continue;
                    }

                    return ip;
                }

                return ipv4Result.get(ipv4Result.size() - 1);
            } else if (!ipv6Result.isEmpty()) {
                return ipv6Result.get(0);
            }
            //If failed to find,fall back to localhost
            final InetAddress localHost = InetAddress.getLocalHost();
            return normalizeHostAddress(localHost);
        } catch (Exception e) {
            log.error("获取本地地址失败!", e);
        }

        return null;
    }

    public static String normalizeHostAddress(final InetAddress localHost) {
        if (localHost instanceof Inet6Address) {
            return "[" + localHost.getHostAddress() + "]";
        } else {
            return localHost.getHostAddress();
        }
    }

    public static SocketAddress string2SocketAddress(final String addr) {
        int split = addr.lastIndexOf(":");
        String host = addr.substring(0, split);
        String port = addr.substring(split + 1);
        InetSocketAddress isa = new InetSocketAddress(host, Integer.parseInt(port));
        return isa;
    }

    public static String socketAddress2String(final SocketAddress addr) {
        StringBuilder sb = new StringBuilder();
        InetSocketAddress inetSocketAddress = (InetSocketAddress) addr;
        sb.append(inetSocketAddress.getAddress().getHostAddress());
        sb.append(":");
        sb.append(inetSocketAddress.getPort());
        return sb.toString();
    }


    public static ChannelProgressivePromise monitor(Channel channel) {
        ChannelProgressivePromise monitor = channel.newProgressivePromise();
        monitor.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) throws Exception {
                log.info("数据正在发送中.....");
            }

            @Override
            public void operationComplete(ChannelProgressiveFuture future) throws Exception {
                log.info("数据已经发送完了！");
            }
        });
        return monitor;
    }

    public static boolean monitor(ChannelFuture channelFuture) {
        Value<Boolean> result = new Value<>(true);
        channelFuture.addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                log.error("消息发送失败!!!!");
                result.setValue(false);
            }
        });
        return result.getValue();
    }


}
