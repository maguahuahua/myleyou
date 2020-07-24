package com.leyou.item.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author chenxm
 * @date 2020/7/3 - 20:00
 */
public interface BrandMapper extends BaseMapper<Brand> {

    //新增中间表
    @Insert("INSERT INTO TB_CATEGORY_BRAND (CATEGORY_ID , BRAND_ID ) VALUES ( #{cid}, #{bid} ) ")
    int insertCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);


    @Select("select b.id , b.name, b.letter from tb_category_brand cb inner join tb_brand b on b.id = cb.brand_id where cb.category_id = #{cid} ")
    List<Brand> queryByCategoryId(@Param("cid") Long cid);
}
