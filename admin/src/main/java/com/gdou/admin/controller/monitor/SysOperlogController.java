package com.gdou.admin.controller.monitor;

import com.gdou.common.annotaion.Log;
import com.gdou.common.core.BaseController;
import com.gdou.common.domain.R;
import com.gdou.common.domain.entity.SysOperLog;
import com.gdou.common.enums.BusinessType;
import com.gdou.system.service.SysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/monitor/operlog")
public class SysOperlogController extends BaseController {

    @Autowired
    private SysOperLogService operLogService;

    @PreAuthorize("@check.hasPermi('monitor:operlog:list')")
    @GetMapping("/list")
    public R list(SysOperLog operLog) {
        startPage();
        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
        return R.success(getPageVo(list));
    }

    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    @PreAuthorize("@check.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/{operIds}")
    public R remove(@PathVariable Long[] operIds) {
        return R.success(operLogService.deleteOperLogByIds(operIds));
    }

    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @PreAuthorize("@check.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/clean")
    public R clean() {
        operLogService.cleanOperLog();
        return R.success("成功！");
    }



}
