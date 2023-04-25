package com.gdou.framework.config.MyBatisPlusConfig;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.gdou.common.domain.model.LoginUser;
import com.gdou.common.utils.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author xzh
 * @time 2022/9/11 22:46
 * <p>
 * 自动填充配置类
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {



    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());

        if (metaObject.hasSetter("loginTime")) {
            this.strictInsertFill(metaObject, "loginTime", Date.class, new Date());
        }

//        //创建者
//        if (metaObject.hasSetter("createBy")) {
//            this.strictInsertFill(metaObject, "createBy", String.class,SecurityUtils.getUsername());
//        }


    }

    @Override
    public void updateFill(MetaObject metaObject) {
        //更新时间
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());

        if (metaObject.hasSetter("loginDate")) {
            this.strictInsertFill(metaObject, "loginDate", Date.class, new Date());
        }

//        if (metaObject.hasSetter("updateBy")) {
//            //创建者
//            this.strictUpdateFill(metaObject, "updateBy", String.class, SecurityUtils.getUsername());
//        }

    }
}