package com.gdou.system.mapper;

import com.gdou.common.domain.entity.SysDictData;

import java.util.List;


public interface SysDictDataMapper{
    List<SysDictData> selectDictDataByType(String dictType);

    List<SysDictData> selectDictData(SysDictData dictData);

    int updateDictDataType(String dictType, String newDictType);

    int selectDictDataCountByType(String dictType);

    SysDictData selectDictDataById(Long dictCode);

    List<SysDictData> selectDictDataList(SysDictData dictData);

    int insertDictData(SysDictData dictData);

    int updateDictData(SysDictData dictData);

    int deleteDictDataById(Long dictCode);
}
