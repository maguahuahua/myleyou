package com.leyou.item.api;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author chenxm
 * @date 2020/7/11 - 20:06
 */
public interface GoodsApi {
    /**
     * 根据spu的id查询详情
     */
    @GetMapping("/spu/detail/{id}")
    SpuDetail queryDetailById(@PathVariable("id") Long spuId);

    /**
     * 根据spuid查询下面的所有sku
     *
     * @param spuId
     * @return
     */
    @GetMapping("/sku/list")
    List<Sku> querySkuById(@RequestParam("id") Long spuId);

    /**
     * 分页查spu
     */
    @GetMapping("/spu/page")
    PageResult<Spu> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", defaultValue = "false") Boolean saleable,
            @RequestParam(value = "key", required = false) String key
    );

    /**
     * 根据spuid查spu
     * @param id
     * @return
     */
    @GetMapping("/spu/{id}")
    Spu querySpuById(@PathVariable("id") Long id);
}
