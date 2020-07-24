package com.leyou.service;

import com.leyou.auth.entity.UserInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.interceptor.LoginInterceptor;
import com.leyou.pojo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chenxm
 * @date 2020/7/21 - 20:18
 */

@Service
public class CartService {
    @Autowired
    private RedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "card:uid";

    public void addCart(Cart cart) {
        //获取登录用户
        UserInfo user = LoginInterceptor.getUserInfo();
        String userKey = KEY_PREFIX + user.getId();
        //当前商品的 hashKey
        String hashkey = cart.getSkuId().toString();
        //记录num
        Integer num = cart.getNum();  //传过来的数量

        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(userKey);
        //判断购物车有当前商品
        if (operation.hasKey(hashkey)) {
            String json = operation.get(hashkey).toString();   //取出原来存在redis中的商品（String）
            cart = JsonUtils.parse(json, Cart.class);  //再转为对象
            cart.setNum(cart.getNum() + num);    //商品数量增加
        }

        //写回redis
        operation.put(hashkey, JsonUtils.serialize(cart));
    }

    public List<Cart> queryCartList() {
        //获取登录用户
        UserInfo user = LoginInterceptor.getUserInfo();
        String userKey = KEY_PREFIX + user.getId();
        if (!redisTemplate.hasKey(userKey)) {
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);
        }
        //获取登录用户购物车
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(userKey);
        List<Cart> carts = operation.values().stream().map(o -> JsonUtils.parse(o.toString(), Cart.class))
                .collect(Collectors.toList());
        return carts;
    }

    public void updateCartNum(Long skuId, Integer num) {
        //获取登录用户
        UserInfo user = LoginInterceptor.getUserInfo();
        String userKey = KEY_PREFIX + user.getId();

        String hashKey = skuId.toString();

        //获取操作
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(userKey);
        if (!operation.hasKey(hashKey)) {
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);
        }
        //查询购物车, operation.get(hashKey)获得的是字符串
        Cart cart = JsonUtils.parse(operation.get(hashKey).toString(), Cart.class);
        cart.setNum(num);

        //写回redis
        operation.put(hashKey, JsonUtils.serialize(cart));
    }

    public void deleteCart(String skuId) {
        //获取登录用户
        UserInfo user = LoginInterceptor.getUserInfo();
        String userKey = KEY_PREFIX + user.getId();

        //删除
        redisTemplate.opsForHash().delete(userKey, skuId.toString());

    }
}
