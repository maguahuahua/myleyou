package com.leyou.service;

import com.github.wxpay.sdk.WXPay;
import com.leyou.auth.entity.UserInfo;
import com.leyou.client.AddressClient;
import com.leyou.client.GoodsClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.IdWorker;
import com.leyou.dto.AddressDTO;
import com.leyou.dto.CartDTO;
import com.leyou.dto.OrderDTO;
import com.leyou.enums.OrderStatusEnum;
import com.leyou.enums.PayState;
import com.leyou.interceptor.LoginInterceptor;
import com.leyou.mapper.OrderDetailMapper;
import com.leyou.mapper.OrderMapper;
import com.leyou.mapper.OrderStatusMapper;
import com.leyou.pojo.Order;
import com.leyou.pojo.OrderDetail;
import com.leyou.pojo.OrderStatus;
import com.leyou.pojo.Sku;
import com.leyou.utils.PayHelper;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.net.ssl.SSLSession;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chenxm
 * @date 2020/7/22 - 12:34
 */

@Service
@Log4j2
public class OrderService {

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderStatusMapper orderStatusMapper;
    @Autowired
    private PayHelper payHelper;


    @Transactional
    public Long createOrder(OrderDTO orderDTO) {
        //1 新增订单
        Order order = new Order();
        //1.1订单编号,基本信息
        long orderId = idWorker.nextId();
        order.setOrderId(orderId);
        order.setCreateTime(new Date());
        order.setPaymentType(orderDTO.getPaymentType());

        //1.2 用户信息
        UserInfo user = LoginInterceptor.getUserInfo();
        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());
        order.setBuyerRate(false);

        //1.3收货人地址
        AddressDTO addr = AddressClient.findById(orderDTO.getAddressId());
        order.setReceiver(addr.getName());
        order.setReceiverAddress(addr.getAddress());
        order.setReceiverCity(addr.getCity());
        order.setReceiverDistrict(addr.getDistrict());
        order.setReceiverMobile(addr.getPhone());
        order.setReceiverState(addr.getState());
        order.setReceiverZip(addr.getZipCode());

        //1.4 金额
        //把cartDTO转为map，键是skuid，值是num
        Map<Long, Integer> numMap = orderDTO.getCarts().stream()
                .collect(Collectors.toMap(CartDTO::getSkuId, CartDTO::getNum));
        Set<Long> ids = numMap.keySet();
        List<Sku> skus = goodsClient.querySkuByIds(new ArrayList<>(ids));//set转为list

        //准备orderDetail集合,遍历时把detail也封装
        ArrayList<OrderDetail> details = new ArrayList<>();

        long totalPay = 0L;
        for (Sku sku : skus) {
            totalPay += sku.getPrice() * numMap.get(sku.getId());

            //封装detail
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setImage(StringUtils.substringBefore(sku.getImages(), ","));
            orderDetail.setNum(numMap.get(sku.getId()));
            orderDetail.setOrderId(orderId);
            orderDetail.setOwnSpec(sku.getOwnSpec());
            orderDetail.setPrice(sku.getPrice());
            orderDetail.setSkuId(sku.getId());
            orderDetail.setTitle(sku.getTitle());
            details.add(orderDetail);
        }
        order.setTotalPay(totalPay);
        //实付金额：总 + 邮费 + 优惠
        order.setActualPay(totalPay + order.getPostFee() - 0);

        // 1.5写入数据库
        int count = orderMapper.insertSelective(order);
        if (count != 1) {
            log.error("创建订单失败" + orderId);
            throw new LyException(ExceptionEnum.CREATE_ORDER_ERROR);
        }

        //2 新增订单详情
        count = orderDetailMapper.insertList(details);
        if (count != details.size()) {
            log.error("创建订单失败" + orderId);
            throw new LyException(ExceptionEnum.CREATE_ORDER_ERROR);
        }

        //3 新增订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setCreateTime(order.getCreateTime());
        orderStatus.setOrderId(orderId);
        orderStatus.setStatus(OrderStatusEnum.INIT.value());
        count = orderStatusMapper.insertSelective(orderStatus);
        if (count != 1) {
            log.error("创建订单失败" + orderId);
            throw new LyException(ExceptionEnum.CREATE_ORDER_ERROR);
        }
        //4 减库存
        List<CartDTO> carts = orderDTO.getCarts();
        goodsClient.decreaseStock(carts);
        return orderId;
    }

    public Order queryOrderById(Long id) {
        //查询订单
        Order order = orderMapper.selectByPrimaryKey(id);
        if (order == null) {
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        //查询订单详情
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(id);
        List<OrderDetail> details = orderDetailMapper.select(orderDetail);
        if (CollectionUtils.isEmpty(details)) {
            throw new LyException(ExceptionEnum.ORDER_DETAIL_NOT_FOUND);
        }
        order.setOrderDetails(details);

        //查询订单状态
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(id);
        if (orderStatus == null) {
            throw new LyException(ExceptionEnum.ORDER_STATUS_NOT_FOUND);
        }
        order.setOrderStatus(orderStatus);
        return order;
    }

    public String createPayUrl(Long orderId) {
        //查询订单
        Order order = queryOrderById(orderId);
        //判断订单状态
        Integer status = order.getOrderStatus().getStatus();
        if (status != OrderStatusEnum.INIT.value()) {
            throw new LyException(ExceptionEnum.ORDER_STATUS_ERROR);
        }

        //支付金额
        Long actualPay = order.getActualPay();
        //商品描述
        OrderDetail orderDetails = order.getOrderDetails().get(0);
        String desc = orderDetails.getTitle();

        return payHelper.createPayUrl(orderId, actualPay, desc);
    }

    public void handleNotify(Map<String, String> result) {
        //数据校验
        payHelper.isSuccess(result);

        //校验签名
        payHelper.isValidSign(result);

        //校验金额
        String totalFeeStr = result.get("total_fee");
        String out_trade_no = result.get("out_trade_no");
        if (StringUtils.isEmpty(totalFeeStr) || StringUtils.isEmpty(out_trade_no)) {
            throw new LyException(ExceptionEnum.INVALID_ORDER_PARAM);
        }

        //结果中的金额
        Long totalFee = Long.valueOf(totalFeeStr);
        //订单中的金额
        Long orderId = Long.valueOf(out_trade_no);
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (totalFee != order.getActualPay()) {
            //金额不符
            throw new LyException(ExceptionEnum.INVALID_ORDER_PARAM);
        }

        //修改订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setStatus(OrderStatusEnum.PAY_UP.value());
        orderStatus.setOrderId(orderId);
        orderStatus.setPaymentTime(new Date());
        int count = orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
        if (count != 1) {
            throw new LyException(ExceptionEnum.UPDATE_ORDER_STATUS_ERROR);
        }
        log.info("[订单回调]，订单支付成功，订单编号：{}", orderId);
    }


    public PayState queryOrderState(Long orderId) {
        //查询订单状态
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(orderId);
        Integer status = orderStatus.getStatus();

        //判断是否支付
        if (status != OrderStatusEnum.INIT.value()) {
            //已支付，是真的支付了
            return PayState.SUCCESS;
        }
        //未支付，不一定是未支付，需要取查支付状态
        return payHelper.queryOrderState(orderId);

    }
}
