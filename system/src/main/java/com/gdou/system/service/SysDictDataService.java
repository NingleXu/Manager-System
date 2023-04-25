package com.gdou.system.service;

import com.gdou.common.domain.PageVo;
import com.gdou.common.domain.entity.SysDictData;

import java.util.List;
import java.util.Map;

public interface SysDictDataService {
    int insertDictData(SysDictData dict);

    int updateDictData(SysDictData dict);

    int deleteDictDataByIds(Long[] dictCodes);

    PageVo<SysDictData> selectDictDataList(Map<String, String> queryCondition);

    SysDictData selectDictDataById(Long dictCode);
}
