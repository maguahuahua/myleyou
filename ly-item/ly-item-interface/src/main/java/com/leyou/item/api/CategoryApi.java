package com.leyou.item.api;

import com.leyou.item.pojo.Category;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author chenxm
 * @date 2020/7/11 - 19:55
 * <p>
 * 提供接口，别的微服务继承
 */

/**
 * 底层是用的动态代理，类似mybatis，对这个接口进行实现，并且把远程调用的代码封装好

@FeignClient("item-service")  //这里知远程调用方，就会去eureka拉取服务列表，拿到之后底层用ribbon实现负载均衡，实现远程调用。

 哪个微服务要用到就继承这个API，并自己写@FeignClient("item-service")，方法写这里是因为只能自己维护这些路径、方法等等
*/

public interface CategoryApi {

    //这里知请求方式、请求路径、请求参数、返回类型
    @GetMapping("category/list/ids")
    List<Category> queryCategoryByIds(@RequestParam("ids") List<Long> ids);
}
