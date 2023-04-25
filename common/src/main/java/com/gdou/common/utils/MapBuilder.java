package com.gdou.common.utils;

import java.util.Map;

/**
 * Map创建类
 *
 * @param <K> Key类型
 * @param <V> Value类型
 * @since 3.1.1
 */
public class MapBuilder<K, V> {

    private final Map<K, V> map;



    /**
     * 创建Builder
     *
     * @param <K> Key类型
     * @param <V> Value类型
     * @param map Map实体类
     * @return MapBuilder
     * @since 3.2.3
     */
    public static <K, V> MapBuilder<K, V> create(Map<K, V> map) {
        return new MapBuilder<>(map);
    }

    /**
     * 链式Map创建类
     *
     * @param map 要使用的Map实现类
     */
    public MapBuilder(Map<K, V> map) {
        this.map = map;
    }

    /**
     * 链式Map创建
     *
     * @param k Key类型
     * @param v Value类型
     * @return 当前类
     */
    public MapBuilder<K, V> put(K k, V v) {
        map.put(k, v);
        return this;
    }

    public Map<K, V> map() {
        return map;
    }


    /**
     * 创建后的map
     *
     * @return 创建后的map
     * @since 3.3.0
     */
    public Map<K, V> build() {
        return map();
    }



}
