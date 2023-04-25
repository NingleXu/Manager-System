package com.gdou.admin.controller;

import com.gdou.common.annotaion.Log;
import com.gdou.common.domain.PageVo;
import com.gdou.common.domain.R;
import com.gdou.common.domain.entity.SysDictType;
import com.gdou.common.enums.BusinessType;
import com.gdou.common.utils.SecurityUtils;
import com.gdou.system.service.SysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.SystemUtils.getUserName;


/**
 * 数据字典信息
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/dict/type")
public class SysDictTypeController {
    @Autowired
    private SysDictTypeService dictTypeService;

    @PreAuthorize("@check.hasPermi('system:dict:list')")
    @GetMapping("/list")
    public R list(@RequestParam  Map<String, String> queryCondition) {
        PageVo<SysDictType> pageVo = dictTypeService.selectDictTypeList(queryCondition);
        return R.success(pageVo);
    }

//    @Log(title = "字典类型", businessType = BusinessType.EXPORT)
//    @PreAuthorize("@ss.hasPermi('system:dict:export')")
//    @PostMapping("/export")
//    public void export(HttpServletResponse response, SysDictType dictType) {
//        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
//        ExcelUtil<SysDictType> util = new ExcelUtil<SysDictType>(SysDictType.class);
//        util.exportExcel(response, list, "字典类型");
//    }

    /**
     * 查询字典类型详细
     */
    @PreAuthorize("@check.hasPermi('system:dict:query')")
    @GetMapping(value = "/{dictId}")
    public R getInfo(@PathVariable Long dictId) {
        return R.success(dictTypeService.selectDictTypeById(dictId));
    }

    /**
     * 新增字典类型
     */
    @PreAuthorize("@check.hasPermi('system:dict:add')")
    @Log(title = "字典类型", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody SysDictType dict) {
        if (dictTypeService.checkDictTypeUnique(dict)) {
            return R.error("新增字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setCreateBy(SecurityUtils.getUsername());
        return R.success(dictTypeService.insertDictType(dict));
    }

    /**
     * 修改字典类型
     */
    @PreAuthorize("@check.hasPermi('system:dict:edit')")
    @Log(title = "字典类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody SysDictType dict) {
        if (dictTypeService.checkDictTypeUnique(dict)) {
            return R.error("修改字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setUpdateBy(SecurityUtils.getUsername());
        return R.success(dictTypeService.updateDictType(dict));
    }

    /**
     * 删除字典类型
     */
    @PreAuthorize("@check.hasPermi('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictIds}")
    public R remove(@PathVariable Long[] dictIds) {
        dictTypeService.deleteDictTypeByIds(dictIds);
        return R.success("成功");
    }

    /**
     * 刷新字典缓存
     */
    @PreAuthorize("@check.hasPermi('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public R refreshCache() {
        dictTypeService.resetDictCache();
        return R.success("成功");
    }

    /**
     * 获取字典选择框列表
     */
    @GetMapping("/optionselect")
    public R optionselect() {
        List<SysDictType> dictTypes = dictTypeService.selectDictTypeAll();
        return R.success(dictTypes);
    }
}
