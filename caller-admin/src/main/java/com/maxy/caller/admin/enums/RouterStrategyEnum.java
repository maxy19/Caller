package com.maxy.caller.admin.enums;

import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * @author maxy
 */

public enum RouterStrategyEnum {


    ROUND((byte) 1, ((addresses) -> {
        AtomicInteger index = RoundRobinHelper.index;//索引：指定开始位置
        String address = Strings.EMPTY;
        for (int i = 0; i < addresses.size(); i++) {
            int nextIndex = index.getAndIncrement() % addresses.size();
            index = new AtomicInteger(nextIndex);
            address = addresses.get(index.get());
            if (index.get() == addresses.size()) {
                index.set(-1);
            }
            return address;
        }
        return null;
    })),

    RANDOM((byte) 2, (addresses -> {
        Collections.shuffle(addresses);
        return addresses.get(0);
    })),

    HASH((byte) 3, (address -> {
        return RouterStrategyHelper.getInstance(address).get(address.get(0));
    })),

    FIRST((byte) 4, (addresses -> {
        return addresses.get(0);
    })),

    LAST((byte) 5, (addresses -> {
        return addresses.get(CollectionUtils.size(addresses) - 1);
    }));

    private Byte code;
    private Function<List<String>, String> function;

    RouterStrategyEnum(Byte code, Function<List<String>, String> function) {
        this.code = code;
        this.function = function;
    }

    public static String router(Byte code, List<String> addresses) {
        for (RouterStrategyEnum currentEnum : values()) {
            if (Objects.equals(currentEnum.code, code)) {
                return currentEnum.function.apply(addresses);
            }
        }
        return null;
    }
}

@Data
class RouterStrategyHelper {

    private RouterStrategyHelper() {
    }

    private static List<String> ipList = new CopyOnWriteArrayList<>();

    private static final class innerClass {
        private static final RouterStrategyHelper ROUTER_STRATEGY_HELPER = new RouterStrategyHelper();
    }

    public static RouterStrategyHelper getInstance(List<String> addresses) {
        if (CollectionUtils.isEmpty(ipList)) {
            ipList.addAll(addresses);
        } else {
            ipList.retainAll(addresses);
        }
        return innerClass.ROUTER_STRATEGY_HELPER;
    }

    public String get(String address) {
        int serverListSize = ipList.size();
        int serverPos = address.hashCode() % serverListSize;
        return ipList.get(serverPos);
    }
}

@Data
class RoundRobinHelper {
    public static AtomicInteger index = new AtomicInteger(0);
}


