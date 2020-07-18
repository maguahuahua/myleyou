package com.leyou.search.client;

import com.leyou.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author chenxm
 * @date 2020/7/11 - 18:09
 */

@FeignClient("item-service")
public interface CategoryClient extends CategoryApi {


}
