package com.leyou.common.mapper;


import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.annotation.RegisterMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author chenxm
 * @date 2020/7/9 - 12:16
 */

@RegisterMapper                                           //一般id是Long
public interface BaseMapper<T> extends Mapper<T>, IdListMapper<T, Long>, InsertListMapper<T> {
}
