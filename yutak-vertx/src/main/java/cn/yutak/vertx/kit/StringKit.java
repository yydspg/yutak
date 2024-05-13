package cn.yutak.vertx.kit;

import java.util.Collection;

public class StringKit {
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
    public static boolean isNotEmpty(String str) {
        return str != null && str.length() > 0;
    }
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.size() == 0;
    }
    public static boolean isNotEmpty(Collection collection) {
        return collection != null && collection.size() > 0;
    }
}
