package com.gdou.admin.controller.monitor;

import com.gdou.common.annotaion.Log;

import com.gdou.common.domain.R;
import com.gdou.common.enums.BusinessType;
import com.gdou.framework.web.service.SysPasswordService;
import com.gdou.system.service.SysLogininforService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * 系统访问记录
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/logininfor")
public class SysLogininforController {
    @Autowired
    private SysLogininforService logininforService;

    @Autowired
    private SysPasswordService passwordService;

    @PreAuthorize("@check.hasPermi('monitor:logininfor:list')")
    @GetMapping("/list")
    public R list(@RequestParam Map<String, String> queryCondition) {
        return R.success(logininforService.selectLogininforList(queryCondition));
    }

    //    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
//    @PreAuthorize("@ss.hasPermi('monitor:logininfor:export')")
//    @PostMapping("/export")
//    public void export(HttpServletResponse response, SysLogininfor logininfor)
//    {
//        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
//        ExcelUtil<SysLogininfor> util = new ExcelUtil<SysLogininfor>(SysLogininfor.class);
//        util.exportExcel(response, list, "登录日志");
//    }
//
    @PreAuthorize("@check.hasPermi('monitor:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    public R remove(@PathVariable Long[] infoIds) {
        return R.success(logininforService.deleteLogininforByIds(infoIds));
    }

    @PreAuthorize("@check.hasPermi('monitor:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public R clean() {
        logininforService.cleanLogininfor();
        return R.success("成功！");
    }

    @PreAuthorize("@check.hasPermi('monitor:logininfor:unlock')")
    @Log(title = "账户解锁", businessType = BusinessType.OTHER)
    @GetMapping("/unlock/{userName}")
    public R unlock(@PathVariable("userName") String userName) {
        passwordService.clearLoginRecordCache(userName);
        return R.success("成功！");
    }
}
