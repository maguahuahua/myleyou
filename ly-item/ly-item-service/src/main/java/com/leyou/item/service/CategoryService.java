package com.leyou.item.service;

import com.leyou.pojo.Category;

import java.util.List;

/**
 * @author chenxm
 * @date 2020/7/1 - 20:01
 */


public interface CategoryService {
    List<Category> queryCategoryListByPid(Long id);

    List<Category> queryByIds(List<Long> cids);
}
