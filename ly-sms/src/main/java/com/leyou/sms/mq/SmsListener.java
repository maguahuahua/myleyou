package com.leyou.sms.mq;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.leyou.common.utils.JsonUtils;
import com.leyou.sms.config.SmsProperties;
import com.leyou.sms.utils.SmsUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * @author chenxm
 * @date 2020/7/18 - 20:27
 */
//@Component
//@EnableConfigurationProperties(SmsProperties.class)
public class SmsListener {

    @Autowired
    private SmsUtils smsUtils;
    @Autowired
    private SmsProperties prop;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.sms.code.queue", durable = "true"),
            exchange = @Exchange(value = "leyou.sms.exchange", type = ExchangeTypes.TOPIC),
            key = {"sms.verify.code"}))
    public void listenSms(Map<String, String> msg) throws Exception {

        if (CollectionUtils.isEmpty(msg)) {
            return;
        }

        String phone = msg.remove("phone");

        if (StringUtils.isBlank(phone)) {
            // 放弃处理
            return;
        }
        // 发送消息
        smsUtils.sendSms(phone, prop.getSignName(), prop.getVerifyCodeTemplate(), JsonUtils.serialize(msg));
    }
}