package com.leyou.item.service;

import com.leyou.common.vo.PageResult;
import com.leyou.pojo.Brand;

import java.util.List;

/**
 * @author chenxm
 * @date 2020/7/1 - 20:01
 */


public interface BrandService {

    PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String sortBy, Boolean desc, String key);

    void saveBrand(Brand brand, List<Long> cids);

    Brand queryById(Long id);

    List<Brand> queryBrandByCid(Long cid);

    List<Brand> queryBrandByIds(List<Long> ids);
}
