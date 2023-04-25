package com.gdou.common.utils;

import com.gdou.common.core.page.PageDomain;
import com.gdou.common.core.page.PageDomainBuilder;
import com.gdou.common.utils.sql.SqlUtil;
import com.gdou.common.utils.text.Convert;
import com.github.pagehelper.PageHelper;

public class PageUtils {

    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "isAsc";

    /**
     * 分页参数合理化
     */
    public static final String REASONABLE = "reasonable";


    /**
     * 设置请求分页数据
     */
    public static void startPage() {
        PageDomain pageDomain = PageDomainBuilder.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
        Boolean reasonable = pageDomain.getReasonable();
        PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(reasonable);
    }

    /**
     * 清理分页的线程变量
     */
    public static void clearPage() {
        PageHelper.clearPage();
    }

}
