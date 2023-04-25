package com.gdou.common.core;


import com.gdou.common.domain.PageVo;
import com.gdou.common.utils.PageUtils;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 进行Web通用数据处理
 */
public class BaseController {

    /**
     * 设置请求分页数据
     */
    protected void startPage() {
        PageUtils.startPage();
    }

    /**
     * 清理分页的线程变量
     */
    protected void clearPage() {
        PageUtils.clearPage();
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected PageVo getPageVo(List<?> list) {
        PageVo pageVo = new PageVo();
        pageVo.setRows(list);
        pageVo.setTotal(new PageInfo(list).getTotal());
        return pageVo;
    }

}
