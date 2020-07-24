package com.leyou.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author chenxm
 * @date 2020/7/22 - 16:08
 */

@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {
}
