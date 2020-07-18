package com.leyou.item.service.impl;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author chenxm
 * @date 2020/7/1 - 20:02
 */

@Service
public class CategoryServiceImpl implements CategoryService {


    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> queryCategoryListByPid(Long id) {
        //查询条件，mapper会根据对象中非空的属性作为查询条件!!!这里即parentId查
        Category category = new Category();
        category.setParentId(id);
        List<Category> list = categoryMapper.select(category);

        //判断结果
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return list;
    }

    //根据多个分类id查分类
    @Override
    public List<Category> queryByIds(List<Long> cids) {
        List<Category> categories = categoryMapper.selectByIdList(cids);
        if (CollectionUtils.isEmpty(categories)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return categories;
    }



}
