package com.leyou.search.client;

import com.leyou.item.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author chenxm
 * @date 2020/7/11 - 20:26
 */

@FeignClient("item-service")

public interface SpecificationClient extends SpecificationApi {
}
