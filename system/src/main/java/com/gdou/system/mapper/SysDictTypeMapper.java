package com.gdou.system.mapper;

import com.gdou.common.domain.entity.SysDictType;

import java.util.List;


public interface SysDictTypeMapper {
    List<SysDictType> selectDictTypeList(SysDictType dictType);

    SysDictType selectDictTypeById(Long dictId);

    List<SysDictType> selectDictTypeAll();

    int insertDictType(SysDictType dictType);

    int updateDictType(SysDictType dict);

    int deleteDictTypeById(Long dictId);

    SysDictType checkDictTypeUnique(String dictType);
}
