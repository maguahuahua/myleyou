package com.leyou.item.web;

import com.leyou.common.vo.PageResult;
import com.leyou.pojo.Brand;
import com.leyou.item.service.impl.BrandServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chenxm
 * @date 2020/7/3 - 20:15
 */

@RestController
@RequestMapping("myBrand")
public class BrandController {
    @Autowired
    private BrandServiceImpl brandService;

    //分页查询品牌
    //PageResult<T>  List<T>，这里的Brand即分页的数据
    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
            @RequestParam(value = "key", required = false) String key
    ) {
        return ResponseEntity.ok(brandService.queryBrandByPage(page, rows, sortBy, desc, key));
    }

    /**
     * 新增品牌         post请求，无返回
     *
     * @param brand 请求数据是Brand的三个属性（除了id自增），
     * @param cids  还有一个cids传过来,mvc把字符串解析为 List
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("categories") List<Long> cids) {
        brandService.saveBrand(brand, cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();    //无返回时可以用build
    }


    /**
     * 根据cid查询品牌
     */

    @GetMapping("/cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCid(@PathVariable("cid") Long cid) {
        return ResponseEntity.ok(brandService.queryBrandByCid(cid));
    }


    /**
     * 根据id主键查品牌
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Brand> queryById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(brandService.queryById(id));
    }


    @GetMapping("brands")
    public ResponseEntity<List<Brand>> queryBrandByIds(@RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(brandService.queryBrandByIds(ids));
    }
}
