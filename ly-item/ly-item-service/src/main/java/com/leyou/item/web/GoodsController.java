package com.leyou.item.web;

import com.leyou.common.vo.PageResult;
import com.leyou.dto.CartDTO;
import com.leyou.pojo.Sku;
import com.leyou.pojo.Spu;
import com.leyou.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chenxm
 * @date 2020/7/8 - 8:59
 */
@RestController
public class GoodsController {
    @Autowired
    private GoodsService goodsService;


    /**
     * 分页查spu
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<Spu>> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", defaultValue = "false") Boolean saleable,
            @RequestParam(value = "key", required = false) String key
    ) {
        return ResponseEntity.ok(goodsService.querySpuByPage(page, rows, saleable, key));
    }


    /**
     * 商品新增
     * 传来的是json，用@RequestBody
     */
    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu) {
        goodsService.saveGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据spu的id查询详情
     *
     * @param spuId
     * @return
     */
    @GetMapping("/spu/detail/{id}")
    public ResponseEntity<SpuDetail> queryDetailById(@PathVariable("id") Long spuId) {
        return ResponseEntity.ok(goodsService.queryDetailById(spuId));
    }

    /**
     * 根据spuid查询下面的所有sku
     *
     * @param spuId
     * @return
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> querySkuById(@RequestParam("id") Long spuId) {
        return ResponseEntity.ok(goodsService.querySkuById(spuId));
    }

    @GetMapping("/spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(goodsService.querySpuById(id));
    }

    /**
     * 根据skuid集合查所有sku
     *
     * @param ids
     * @return
     */
    @GetMapping("sku/list/ids")
    public ResponseEntity<List<Sku>> querySkuByIds(@RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(goodsService.querySkuByIds(ids));
    }

    /**
     * 减库存
     *
     * @param carts
     * @return
     */
    @PostMapping("stock/decrease")
    public ResponseEntity<Void> decreaseStock(@RequestBody List<CartDTO> carts) {
        goodsService.decreaseStock(carts);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
