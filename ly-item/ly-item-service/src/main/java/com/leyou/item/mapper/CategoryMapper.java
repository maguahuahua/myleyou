package com.leyou.item.mapper;

import com.leyou.pojo.Category;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author chenxm
 * @date 2020/7/1 - 20:00
 */
public interface CategoryMapper extends Mapper<Category>, IdListMapper<Category,Long> {

}
