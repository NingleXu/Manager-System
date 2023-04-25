package com.gdou.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页列表
 * @author xzh
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageVo<T> {

    /**
     * 分页列表
     */
    private List<T> rows;

    /**
     * 总数
     */
    private Long total;

}
