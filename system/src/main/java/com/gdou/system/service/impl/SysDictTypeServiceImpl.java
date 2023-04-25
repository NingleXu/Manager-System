package com.gdou.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdou.common.domain.PageVo;
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


import static com.gdou.common.constant.Constants.STATUS_RUNNING;
import static com.gdou.common.constant.PageConstants.*;
import static com.gdou.common.constant.UserConstants.EXIST;
import static com.gdou.common.utils.DictUtil.*;


@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements SysDictTypeService {

    @Autowired
    private SysDictDataMapper dictDataMapper;

    /**
     * @author xzh
     * @time 2023/4/10 21:24
     * 分页条件查询
     */
    @Override
    public PageVo<SysDictType> selectDictTypeList(Map<String, String> queryCondition) {
        LambdaQueryWrapper<SysDictType> queryWrapper = new LambdaQueryWrapper<>();
        String startTime = queryCondition.get(BEGIN_TIME);
        String endTime = queryCondition.get(END_TIME);

        queryWrapper.like(StringUtils.isNotEmpty(queryCondition.get(DICT_NAME)),
                        SysDictType::getDictName, queryCondition.get(DICT_NAME))
                .like(StringUtils.isNotEmpty(queryCondition.get(DICT_TYPE)),
                        SysDictType::getDictType, queryCondition.get(DICT_TYPE))
                .eq(StringUtils.isNotEmpty(queryCondition.get(STATUS)),
                        SysDictType::getStatus, queryCondition.get(STATUS))
                .between(StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime),
                        SysDictType::getCreateTime, startTime, endTime);

        Page<SysDictType> page = new Page<>(Long.parseLong(queryCondition.get(PAGE_NUM)),
                Long.parseLong(queryCondition.get(PAGE_SIZE)));
        baseMapper.selectPage(page, queryWrapper);
        return new PageVo<>(page.getRecords(), page.getTotal());
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
        List<SysDictData> sysDictData = dictDataMapper
                .selectList(new LambdaQueryWrapper<SysDictData>()
                        .eq(SysDictData::getDictType, dictType)
                        .eq(SysDictData::getStatus, STATUS_RUNNING));
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
        return baseMapper.selectById(dictId);
    }

    /**
     * @author xzh
     * @time 2023/4/11 19:40
     * 查询所有字典类型
     */
    @Override
    public List<SysDictType> selectDictTypeAll() {
        return baseMapper.selectList(null);
    }

    /**
     * 加载字典缓存数据
     */
    @Override
    public void loadingDictCache() {
        Map<String, List<SysDictData>> dictDataMap = dictDataMapper.selectList(new LambdaQueryWrapper<SysDictData>()
                        .eq(SysDictData::getStatus, STATUS_RUNNING))
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
    public int insertDictType(SysDictType dict) {
        int count = baseMapper.insert(dict);
        //插入成功加入缓存
        if (count > 0) {
            setDictCache(dict.getDictType(), null);
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
        int count = baseMapper.updateById(dict);
        if (count > 0) {
            setDictCache(dict.getDictType(), dictDataMapper.selectList(new LambdaQueryWrapper<SysDictData>()
                    .eq(SysDictData::getDictType, dict.getDictType())));
        }
        return count;
    }

    /**
     * @author xzh
     * @time 2023/4/11 20:40
     * 批量删除
     */
    @Override
    public void deleteDictTypeByIds(Long[] dictIds) {
        for (Long dictId : dictIds) {
            SysDictType dictType = baseMapper.selectById(dictId);
            if (dictDataMapper.selectCount(new LambdaQueryWrapper<SysDictData>()
                    .eq(SysDictData::getDictType, dictType.getDictType())) >= EXIST) {
                throw new ServiceException(String.format("%1$s已分配,不能删除", dictType.getDictName()));
            }
            baseMapper.deleteById(dictId);
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
        return baseMapper.selectCount(new LambdaQueryWrapper<SysDictType>()
                .ne(StringUtils.isNotNull(dict.getDictId()), SysDictType::getDictId, dict.getDictId())
                .eq(SysDictType::getDictType, dict.getDictType())) >= EXIST;
    }


}
