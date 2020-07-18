package com.leyou.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.item.service.BrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author chenxm
 * @date 2020/7/3 - 20:14
 */
@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandMapper brandMapper;

    @Override
    public PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        //分页
        PageHelper.startPage(page, rows);  //利用mvc的过滤器，加上limit条件?

        /**
         * selece * from xxx
         * where 'name' like ""%xxx% or letter == 'x'
         * order by id desc
         * 通用mapper就用Example实现，单表查询基本都可以
         */

        //过滤
        Example example = new Example(Brand.class);
        if (StringUtils.isNotBlank(key)) {
            //过滤条件
            example.createCriteria().orLike("name", "%" + key + "%")
                    .orEqualTo("letter", key.toUpperCase());
        }
        //排序
        if (StringUtils.isNotBlank(sortBy)) {
            example.setOrderByClause(sortBy + (desc ? " DESC" : " ASC"));
        }

        //查询
        List<Brand> list = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        //解析分页结果
        PageInfo<Brand> brandPageInfo = new PageInfo<>(list);      //PageInfo就是一个分页Bean，多了pageNum等等
        return new PageResult<Brand>(brandPageInfo.getTotal(), list);  //要多返回总数
    }

    @Override
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        //新增品牌
        int conut = brandMapper.insert(brand);
        if (conut != 1) {
            throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
        }

        //11aa
        //新增中间表
        for (Long cid : cids) {
            conut = brandMapper.insertCategoryBrand(cid, brand.getId());
            if (conut != 1) {
                throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
            }
        }
    }

    //ces
    @Override
    public Brand queryById(Long id) {
        Brand brand = brandMapper.selectByPrimaryKey(id);
        if (brand == null) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brand;
    }


    @Override
    public List<Brand> queryBrandByCid(Long cid) {
        List<Brand> list = brandMapper.queryByCategoryId(cid);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return list;
    }

    @Override
    public List<Brand> queryBrandByIds(List<Long> ids) {
        List<Brand> brands = brandMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(brands)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brands;
    }
}
