package com.leyou.web;

import com.leyou.pojo.Cart;
import com.leyou.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author chenxm
 * @date 2020/7/21 - 20:17
 */

//TODO 均未测试！！前端问题

@RestController
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart) {
        cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 查询购物车列表
     *
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Cart>> queryCartList() {
        return ResponseEntity.ok(cartService.queryCartList());
    }

    /**
     * 修改购物车商品数量
     *
     * @param skuId
     * @param num
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateCartNum(@RequestParam("id") Long skuId, @RequestParam("num") Integer num) {
        cartService.updateCartNum(skuId, num);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId") String skuId) {
        cartService.deleteCart(skuId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}