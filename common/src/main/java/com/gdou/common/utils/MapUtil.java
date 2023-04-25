package com.gdou.common.utils;

import java.util.HashMap;

/**
 * @author xzh
 * @time 2023/4/3 15:08
 * 创建map
 */
public class MapUtil {
    /**
     * 创建链接调用map
     *
     * @param <K> Key类型
     * @param <V> Value类型
     * @return map创建类
     */
    public static <K, V> MapBuilder<K, V> builder() {
        return builder(new HashMap<>());
    }

    private static <V, K> MapBuilder<K,V> builder(HashMap<K,V> map) {
        return new MapBuilder<>(map);
    }
}
