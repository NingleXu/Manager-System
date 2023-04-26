package com.gdou.system.service;

import com.gdou.common.domain.PageVo;
import com.gdou.common.domain.entity.SysDictData;
import com.gdou.common.domain.entity.SysDictType;

import java.util.List;

public interface SysDictTypeService {
    List<SysDictType> selectDictTypeList(SysDictType dictType);

    List<SysDictData> selectDictDataByType(String dictType);

    SysDictType selectDictTypeById(Long dictId);

    List<SysDictType> selectDictTypeAll();


    void loadingDictCache();

    void resetDictCache();

    boolean checkDictTypeUnique(SysDictType dict);

    int insertDictType(SysDictType dict);

    int updateDictType(SysDictType dict);

    void deleteDictTypeByIds(Long[] dictIds);

}
