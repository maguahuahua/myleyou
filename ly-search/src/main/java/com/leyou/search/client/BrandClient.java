package com.leyou.search.client;

import com.leyou.item.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author chenxm
 * @date 2020/7/11 - 20:27
 */

@FeignClient("item-service")
public interface BrandClient extends BrandApi {
}
