package com.gdou.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdou.common.constant.CacheConstants;
import com.gdou.common.domain.PageVo;
import com.gdou.common.domain.entity.SysDictData;
import com.gdou.common.utils.RedisCache;
import com.gdou.common.utils.StringUtils;
import com.gdou.system.mapper.SysDictDataMapper;
import com.gdou.system.service.SysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.gdou.common.constant.Constants.ZERO;
import static com.gdou.common.constant.PageConstants.*;


@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements SysDictDataService {

    @Autowired
    private RedisCache redisCache;

    @Override
    public SysDictData selectDictDataById(Long dictCode) {
        return baseMapper.selectById(dictCode);
    }

    /**
     * @author xzh
     * @time 2023/4/10 21:08
     * 条件分页查询数据字典
     */
    @Override
    public PageVo<SysDictData> selectDictDataList(Map<String, String> queryCondition) {

        LambdaQueryWrapper<SysDictData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(queryCondition.get(DICT_TYPE)),
                        SysDictData::getDictType, queryCondition.get(DICT_TYPE))
                .like(StringUtils.isNotEmpty(queryCondition.get(DICT_LABEL)),
                        SysDictData::getDictLabel, queryCondition.get(DICT_LABEL))
                .eq(StringUtils.isNotEmpty(queryCondition.get(STATUS)),
                        SysDictData::getStatus, queryCondition.get(STATUS));
        Page<SysDictData> page = new Page<>(Long.parseLong(queryCondition.get(PAGE_NUM)),
                Long.parseLong(queryCondition.get(PAGE_SIZE)));

        baseMapper.selectPage(page, queryWrapper);

        return new PageVo<>(page.getRecords(), page.getTotal());
    }

    /**
     * @author xzh
     * @time 2023/4/10 10:32
     * 创建数据字典
     */
    @Override
    public int insertDictData(SysDictData dict) {
        int count = this.baseMapper.insert(dict);
        if (count > ZERO) {
            List<SysDictData> sysDictData = this.baseMapper
                    .selectList(new LambdaQueryWrapper<SysDictData>()
                            .eq(SysDictData::getDictType, dict.getDictType()));
            redisCache.setCacheObject(CacheConstants.SYS_DICT_KEY + dict.getDictType(), sysDictData);
        }
        return count;
    }

    /**
     * @author xzh
     * @time 2023/4/10 10:32
     * 修改数据字典
     */
    @Override
    public int updateDictData(SysDictData dict) {
        int count = this.baseMapper.updateById(dict);
        List<SysDictData> sysDictData = this.baseMapper
                .selectList(new LambdaQueryWrapper<SysDictData>()
                        .eq(SysDictData::getDictCode, dict));
        redisCache.setCacheObject(CacheConstants.SYS_DICT_KEY + dict.getDictType(), sysDictData);
        return count;
    }

    /**
     * @author xzh
     * @time 2023/4/10 10:32
     * 删除数据字典
     */
    @Override
    public int deleteDictDataByIds(Long[] dictCodes) {
        //批量删除
        List<SysDictData> sysDictDatas = this.baseMapper
                .selectList(new LambdaQueryWrapper<SysDictData>()
                        .in(SysDictData::getDictCode, dictCodes));

        for (SysDictData sysDictData : sysDictDatas) {
            this.baseMapper.deleteById(sysDictData.getDictCode());

            List<SysDictData> typeDate = baseMapper.selectList(new LambdaQueryWrapper<SysDictData>()
                    .eq(SysDictData::getDictType, sysDictData.getDictType()));
            //删除缓存
            redisCache.setCacheObject(CacheConstants.SYS_DICT_KEY + sysDictData.getDictType(), typeDate);
        }
        return sysDictDatas.size();
    }
}
