package com.leyou.item.service;

import com.leyou.common.vo.PageResult;
import com.leyou.dto.CartDTO;
import com.leyou.pojo.Sku;
import com.leyou.pojo.Spu;
import com.leyou.pojo.SpuDetail;

import java.util.List;

/**
 * @author chenxm
 * @date 2020/7/8 - 9:04
 */
public interface GoodsService {
    PageResult<Spu> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key);

    void saveGoods(Spu spu);

    SpuDetail queryDetailById(Long spuId);

    List<Sku> querySkuById(Long spuId);

    Spu querySpuById(Long id);

    List<Sku> querySkuByIds(List<Long> ids);

    void decreaseStock(List<CartDTO> carts);
}
