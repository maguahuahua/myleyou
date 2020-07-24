package com.leyou.item.api;

import com.leyou.pojo.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author chenxm
 * @date 2020/7/11 - 20:22
 */
public interface BrandApi {
    /**
     * 根据id主键查品牌
     */
    @GetMapping("myBrand/{id}")
    Brand queryById(@PathVariable("id") Long id);

    @GetMapping("myBrand/brands")
    List<Brand> queryBrandByIds(@RequestParam("ids") List<Long> ids);
}
