package com.leyou.search.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author chenxm
 * @date 2020/7/11 - 20:09
 */

@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {
}
