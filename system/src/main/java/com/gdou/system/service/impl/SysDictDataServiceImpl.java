package com.gdou.system.service.impl;


import com.gdou.common.constant.CacheConstants;
import com.gdou.common.domain.entity.SysDictData;
import com.gdou.common.utils.RedisCache;
import com.gdou.system.mapper.SysDictDataMapper;
import com.gdou.system.service.SysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.gdou.common.constant.Constants.ZERO;
import static com.gdou.common.utils.DictUtil.setDictCache;


@Service
public class SysDictDataServiceImpl implements SysDictDataService {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private SysDictDataMapper dictDataMapper;

    @Override
    public SysDictData selectDictDataById(Long dictCode) {
        return dictDataMapper.selectDictDataById(dictCode);
    }

    /**
     * @author xzh
     * @time 2023/4/10 21:08
     * 条件分页查询数据字典
     */
    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData) {
        return dictDataMapper.selectDictDataList(dictData);
    }

    /**
     * @author xzh
     * @time 2023/4/10 10:32
     * 创建数据字典
     */
    @Override
    public int insertDictData(SysDictData dictData) {
        int count = dictDataMapper.insertDictData(dictData);
        if (count > ZERO) {
            List<SysDictData> sysDictData = dictDataMapper.selectDictDataByType(dictData.getDictType());
            redisCache.setCacheObject(CacheConstants.SYS_DICT_KEY + dictData.getDictType(), sysDictData);
        }
        return count;
    }

    /**
     * @author xzh
     * @time 2023/4/10 10:32
     * 修改数据字典
     */
    @Override
    public int updateDictData(SysDictData dictData) {
        int count = dictDataMapper.updateDictData(dictData);
        if (count > ZERO) {
            List<SysDictData> sysDictData = dictDataMapper.selectDictDataByType(dictData.getDictType());
            redisCache.setCacheObject(CacheConstants.SYS_DICT_KEY + dictData.getDictType(), sysDictData);
        }
        return count;
    }

    /**
     * @author xzh
     * @time 2023/4/10 10:32
     * 删除数据字典
     */
    @Override
    public int deleteDictDataByIds(Long[] dictCodes) {
        for (Long dictCode : dictCodes) {
            SysDictData data = selectDictDataById(dictCode);
            dictDataMapper.deleteDictDataById(dictCode);
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(data.getDictType());
            setDictCache(data.getDictType(), dictDatas);
        }
        return dictCodes.length;
    }
}
