package com.gdou.system.service.impl;


import com.gdou.common.domain.entity.SysDictData;
import com.gdou.common.domain.entity.SysDictType;
import com.gdou.common.exception.ServiceException;
import com.gdou.common.utils.DictUtil;
import com.gdou.common.utils.StringUtils;
import com.gdou.system.mapper.SysDictDataMapper;
import com.gdou.system.mapper.SysDictTypeMapper;
import com.gdou.system.service.SysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.gdou.common.constant.UserConstants.*;
import static com.gdou.common.utils.DictUtil.*;


@Service
public class SysDictTypeServiceImpl implements SysDictTypeService {

    @Autowired
    private SysDictDataMapper dictDataMapper;

    @Autowired
    private SysDictTypeMapper dictTypeMapper;

    /**
     * @author xzh
     * @time 2023/4/10 21:24
     * 分页条件查询
     */
    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType) {
        return dictTypeMapper.selectDictTypeList(dictType);
    }

    /**
     * @author xzh
     * @time 2023/4/11 15:00
     * 根据字典类型查询 字典内容
     */
    @Override
    public List<SysDictData> selectDictDataByType(String dictType) {

        List<SysDictData> dictCache = DictUtil.getDictCache(dictType);
        //存在缓存
        if (StringUtils.isNotEmpty(dictCache)) {
            return dictCache;
        }
        //不存在
        List<SysDictData> sysDictData = dictDataMapper.selectDictDataByType(dictType);
        if (StringUtils.isNotEmpty(sysDictData)) {
            setDictCache(dictType, sysDictData);
        }
        return sysDictData;
    }

    /**
     * @author xzh
     * @time 2023/4/11 15:57
     * 根据类型id查询SysDictData
     */
    @Override
    public SysDictType selectDictTypeById(Long dictId) {
        return dictTypeMapper.selectDictTypeById(dictId);
    }

    /**
     * @author xzh
     * @time 2023/4/11 19:40
     * 查询所有字典类型
     */
    @Override
    public List<SysDictType> selectDictTypeAll() {
        return dictTypeMapper.selectDictTypeAll();
    }

    /**
     * 加载字典缓存数据
     */
    @Override
    public void loadingDictCache() {
        SysDictData dictData = new SysDictData();
        dictData.setStatus("0");
        Map<String, List<SysDictData>> dictDataMap = dictDataMapper.selectDictData(dictData)
                .stream().collect(Collectors.groupingBy(SysDictData::getDictType));
        for (Map.Entry<String, List<SysDictData>> entry : dictDataMap.entrySet()) {
            setDictCache(entry.getKey(), entry.getValue().stream().sorted(Comparator.comparing(SysDictData::getDictSort)).collect(Collectors.toList()));
        }
    }

    /**
     * @author xzh
     * @time 2023/4/11 19:40
     * 清除所有字典缓存
     */
    @Override
    public void resetDictCache() {
        clearDictCache();
        loadingDictCache();
    }

    /**
     * @author xzh
     * @time 2023/4/11 20:37
     * 插入新字典类型
     */
    @Override
    public int insertDictType(SysDictType dictType) {
        int count = dictTypeMapper.insertDictType(dictType);
        //插入成功加入缓存
        if (count > 0) {
            setDictCache(dictType.getDictType(), null);
        }
        return count;
    }

    /**
     * @author xzh
     * @time 2023/4/11 20:38
     * 修改字典
     */
    @Override
    @Transactional
    public int updateDictType(SysDictType dict) {
        SysDictType oldDict = dictTypeMapper.selectDictTypeById(dict.getDictId());
        dictDataMapper.updateDictDataType(oldDict.getDictType(), dict.getDictType());
        int row = dictTypeMapper.updateDictType(dict);
        if (row > 0) {
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(dict.getDictType());
            setDictCache(dict.getDictType(), dictDatas);
        }
        return row;
    }

    /**
     * @author xzh
     * @time 2023/4/11 20:40
     * 批量删除
     */
    @Override
    public void deleteDictTypeByIds(Long[] dictIds) {
        for (Long dictId : dictIds) {
            SysDictType dictType = dictTypeMapper.selectDictTypeById(dictId);
            if (dictDataMapper.selectDictDataCountByType(dictType.getDictType()) > 0) {
                throw new ServiceException(String.format("%1$s已分配,不能删除", dictType.getDictName()));
            }
            dictTypeMapper.deleteDictTypeById(dictId);
            removeDictCache(dictType.getDictType());
        }
    }

    /**
     * @author xzh
     * @time 2023/4/11 20:37
     * 校验是否重名
     */
    @Override
    public boolean checkDictTypeUnique(SysDictType dict) {
        Long dictId = StringUtils.isNull(dict.getDictId()) ? -1L : dict.getDictId();
        SysDictType dictType = dictTypeMapper.checkDictTypeUnique(dict.getDictType());
        if (StringUtils.isNotNull(dictType) && dictType.getDictId().longValue() != dictId){
            return NOT_UNIQUE;
        }
        return UNIQUE;
    }


}
