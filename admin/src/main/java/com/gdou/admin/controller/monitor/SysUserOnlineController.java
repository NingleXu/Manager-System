package com.gdou.admin.controller.monitor;

import com.gdou.common.annotaion.Log;
import com.gdou.common.constant.CacheConstants;
import com.gdou.common.domain.PageVo;
import com.gdou.common.domain.R;
import com.gdou.common.domain.entity.SysUserOnline;
import com.gdou.common.domain.model.LoginUser;
import com.gdou.common.enums.BusinessType;
import com.gdou.common.utils.RedisCache;
import com.gdou.common.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/monitor/online")
public class SysUserOnlineController {

    @Autowired
    private RedisCache redisCache;

    @PreAuthorize("@check.hasPermi('monitor:online:list')")
    @GetMapping("/list")
    public R list(String ipaddr, String userName) {
        Collection<String> keys = redisCache.keys(CacheConstants.LOGIN_TOKEN_KEY + "*");
        List<SysUserOnline> userOnlineList = new ArrayList<>();
        for (String key : keys) {
            LoginUser user = redisCache.getCacheObject(key);
            SysUserOnline sysUserOnline = new SysUserOnline();
            sysUserOnline.setTokenId(key.substring(13));
            BeanUtils.copyProperties(user, sysUserOnline);
            if (StringUtils.isNotNull(ipaddr) && StringUtils.isNotNull(userName)
                    && sysUserOnline.getIpaddr().equals(ipaddr) && sysUserOnline.getUserName().contains(userName)) {
                userOnlineList.add(sysUserOnline);
            } else if (StringUtils.isNotNull(ipaddr) && sysUserOnline.getIpaddr().equals(ipaddr)) {
                userOnlineList.add(sysUserOnline);
            } else if (StringUtils.isNotNull(userName) && sysUserOnline.getUserName().contains(userName)) {
                userOnlineList.add(sysUserOnline);
            } else {
                userOnlineList.add(sysUserOnline);
            }
        }
        Collections.reverse(userOnlineList);
        return R.success(new PageVo<>(userOnlineList, (long) userOnlineList.size()));
    }


    @PreAuthorize("@check.hasPermi('monitor:online:forceLogout')")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/{tokenId}")
    public R forceLogout(@PathVariable String tokenId) {
        redisCache.deleteObject(CacheConstants.LOGIN_TOKEN_KEY + tokenId);
        return R.success("成功!");
    }


}