package com.leyou.utils;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;

import static com.github.wxpay.sdk.WXPayConstants.*;

import com.github.wxpay.sdk.WXPayUtil;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.config.PayConfig;
import com.leyou.enums.OrderStatusEnum;
import com.leyou.enums.PayState;
import com.leyou.mapper.OrderMapper;
import com.leyou.mapper.OrderStatusMapper;
import com.leyou.pojo.Order;
import com.leyou.pojo.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenxm
 * @date 2020/7/23 - 15:12
 */

@Slf4j
@Component
public class PayHelper {

    @Autowired
    private WXPay wxPay;

    @Autowired
    private PayConfig payConfig;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderStatusMapper orderStatusMapper;

    public PayState queryOrderState(Long orderId) {
        try {
            HashMap<String, String> data = new HashMap<>();
            //订单号
            data.put("out_trade_no", orderId.toString());
            //查询状态
            Map<String, String> result = wxPay.orderQuery(data);

            //校验状态
            isSuccess(result);
            //校验状态
            isValidSign(result);

            //校验金额
            String totalFeeStr = result.get("total_fee");
            String out_trade_no = result.get("out_trade_no");
            if (StringUtils.isEmpty(totalFeeStr) || StringUtils.isEmpty(out_trade_no)) {
                throw new LyException(ExceptionEnum.INVALID_ORDER_PARAM);
            }
            //结果中的金额
            Long totalFee = Long.valueOf(totalFeeStr);
            //订单中的金额
            orderId = Long.valueOf(out_trade_no);
            Order order = orderMapper.selectByPrimaryKey(orderId);
            if (totalFee != order.getActualPay()) {
                //金额不符
                throw new LyException(ExceptionEnum.INVALID_ORDER_PARAM);
            }


            String state = result.get("trade_state");
            if (SUCCESS.equals(state)) {
                //支付成功
                //修改订单状态
                OrderStatus orderStatus = new OrderStatus();
                orderStatus.setStatus(OrderStatusEnum.PAY_UP.value());
                orderStatus.setOrderId(orderId);
                orderStatus.setPaymentTime(new Date());
                int count = orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
                if (count != 1) {
                    throw new LyException(ExceptionEnum.UPDATE_ORDER_STATUS_ERROR);
                }
                return PayState.SUCCESS;
            }
            if ("NOTPAY".equals(state) || "USERPAYING".equals(state)) {
                return PayState.NOT_PAY;
            }

            return PayState.FAIL;
        } catch (Exception e) {
            return PayState.NOT_PAY;
        }
    }

    public String createPayUrl(Long orderId, Long totalPay, String description) {
        try {
            HashMap<String, String> data = new HashMap<>();
            //描述
            data.put("body", description);
            //订单号
            data.put("out_trade_no", orderId.toString());
            //货币（默认就是人民币）
            //data.put("fee_type", "CNY");
            //总金额
            data.put("total_fee", totalPay.toString());
            //调用微信支付的终端ip
            data.put("spbill_create_ip", "127.0.0.1");
            //回调地址
            data.put("notify_url", payConfig.getNotifyUrl());
            //交易类型为扫码支付
            data.put("trade_type", "NATIVE");

            //利用wxPay工具，完成下单
            Map<String, String> result = wxPay.unifiedOrder(data);

            isSuccess(result);

            //下单成功，获取支付连接
            String url = result.get("code_url");
            return url;
        } catch (Exception e) {
            log.error("【微信下单】创建预交易订单失败", e);
            return null;
        }
    }

    public void isSuccess(Map<String, String> result) {
        //判断通信是否成功
        String return_code = result.get("return_code");
        if (FAIL.equals(return_code)) {
            log.error("【微信下单】与微信通信失败，失败信息：{}", result.get("return_msg"));
            throw new LyException(ExceptionEnum.WX_PAY_ORDER_FAIL);
        }
        //判断业务标识
        String result_code = result.get("result_code");
        System.out.println(result_code);
        if (FAIL.equals(result_code)) {
            log.error("【微信下单】与微信业务失败：{}，失败信息：{}", result.get("err_code"), result.get("err_code_des"));
            throw new LyException(ExceptionEnum.WX_PAY_ORDER_FAIL);
        }
    }

    //data就是微信返回的xml数据反序列化为map
    public void isValidSign(Map<String, String> data) {
        try {
            //重写生成签名
            String sign1 = WXPayUtil.generateSignature(data, payConfig.getKey(), SignType.HMACSHA256);
            String sign2 = WXPayUtil.generateSignature(data, payConfig.getKey(), SignType.MD5);

            //和传过来的签名对比
            String sign = data.get("sign");

            if (!StringUtils.equals(sign, sign1) && !StringUtils.equals(sign, sign2)) {
                //签名有误
                throw new LyException(ExceptionEnum.INVALID_SIGN_ERROR);
            }

        } catch (Exception e) {
            throw new LyException(ExceptionEnum.INVALID_SIGN_ERROR);
        }
    }
}

