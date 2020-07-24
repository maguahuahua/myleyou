package com.leyou.web;

import com.leyou.dto.OrderDTO;
import com.leyou.pojo.Order;
import com.leyou.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author chenxm
 * @date 2020/7/22 - 12:33
 */

@RestController
@RequestMapping("order")  //"order"
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     *
     * @param orderDTO
     * @return
     */
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.createOrder(orderDTO));
    }

    /**
     * 根据id查订单
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Order> queryOrderById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.queryOrderById(id));
    }

    /**
     * 创建支付链接
     *
     * @param orderId
     * @return
     */
    @GetMapping("/url/{id}")
    public ResponseEntity<String> createPayUrl(@PathVariable("id") Long orderId) {
        String payUr = orderService.createPayUrl(orderId);
        return ResponseEntity.ok(payUr);
    }

    @GetMapping("/state/{id}")
    public ResponseEntity<Integer> queryOrderState(@PathVariable("id") Long orderId) {
        return ResponseEntity.ok(orderService.queryOrderState(orderId).getValue());
    }
}
