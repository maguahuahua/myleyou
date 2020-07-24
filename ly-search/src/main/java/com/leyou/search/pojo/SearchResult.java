package com.leyou.search.pojo;

import com.leyou.common.vo.PageResult;
import com.leyou.pojo.Brand;
import com.leyou.pojo.Category;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author chenxm
 * @date 2020/7/15 - 19:10
 */

//搜索过滤时，加上分类和品牌字段，因为之前的分页没有这两个

@Data
public class SearchResult extends PageResult<Goods> {
    private List<Category> categories;  //分类待选项
    private List<Brand> brands;         //品牌待选项

    private List<Map<String, Object>> specs;   //规格参数，要根据用户查询的商品的分类确定

    public SearchResult() {
    }

    public SearchResult(Long total, Integer totalPage, List<Goods> items, List<Category> categories, List<Brand> brands, List<Map<String, Object>> specs) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }


}
