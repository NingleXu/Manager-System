package com.gdou.admin.controller;


import com.gdou.common.annotaion.Log;
import com.gdou.common.core.BaseController;
import com.gdou.common.domain.R;
import com.gdou.common.domain.entity.SysDictData;
import com.gdou.common.enums.BusinessType;
import com.gdou.system.service.SysDictDataService;
import com.gdou.system.service.SysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.gdou.common.utils.SecurityUtils.getUsername;

/**
 * 数据字典信息
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController extends BaseController {
    @Autowired
    private SysDictDataService dictDataService;

    @Autowired
    private SysDictTypeService dictTypeService;

    @PreAuthorize("@check.hasPermi('system:dict:list')")
    @GetMapping("/list")
    public R list(SysDictData dictData) {
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        return R.success(getPageVo(list));
    }

//    @Log(title = "字典数据", businessType = BusinessType.EXPORT)
//    @PreAuthorize("@check.hasPermi('system:dict:export')")
//    @PostMapping("/export")
//    public void export(HttpServletResponse response, SysDictData dictData)
//    {
//        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
//        ExcelUtil<SysDictData> util = new ExcelUtil<SysDictData>(SysDictData.class);
//        util.exportExcel(response, list, "字典数据");
//    }

    /**
     * 查询字典数据详细
     */
    @PreAuthorize("@check.hasPermi('system:dict:query')")
    @GetMapping(value = "/{dictCode}")
    public R getInfo(@PathVariable Long dictCode) {
        return R.success(dictDataService.selectDictDataById(dictCode));
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping(value = "/type/{dictType}")
    public R dictType(@PathVariable String dictType) {
        List<SysDictData> data = dictTypeService.selectDictDataByType(dictType);
        return R.success(data);
    }

    /**
     * 新增字典类型
     */
    @PreAuthorize("@check.hasPermi('system:dict:add')")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@Validated @RequestBody SysDictData dict) {
        dict.setCreateBy(getUsername());
        return R.success(dictDataService.insertDictData(dict));
    }

    /**
     * 修改保存字典类型
     */
    @PreAuthorize("@check.hasPermi('system:dict:edit')")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@Validated @RequestBody SysDictData dict) {
        dict.setUpdateBy(getUsername());
        return R.success(dictDataService.updateDictData(dict));
    }

    /**
     * 删除字典类型
     */
    @PreAuthorize("@check.hasPermi('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictCodes}")
    public R remove(@PathVariable Long[] dictCodes) {
        dictDataService.deleteDictDataByIds(dictCodes);
        return R.success("删除成功！");
    }
}
