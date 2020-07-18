package com.leyou.page.mq;

import com.leyou.page.service.PageService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author chenxm
 * @date 2020/7/17 - 15:13
 */

@Component
public class ItemListener {

    @Autowired
    private PageService pageService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "page.item.insert.queue", durable = "true"),   //这个name随便起
            exchange = @Exchange(name = "ly.item.exchange", type = ExchangeTypes.TOPIC),  //name与配置文件的对应
            key = {"item.insert", "item.update"}                                          //匹配发的mq
    ))
    public void listenCreateOrUpdate(Long spuId) {
        if (spuId == null) {
            return;
        }
        //处理消息，对索引库新增或者修改。
        //异常等spring捕获处理消息，ack是默认打开的，异常不会发送ack  ？
        pageService.createHtml(spuId);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "page.item.delete.queue", durable = "true"),   //这个name随便起
            exchange = @Exchange(name = "ly.item.exchange", type = ExchangeTypes.TOPIC),  //name与配置文件的对应
            key = {"item.delete"}                                          //匹配发的mq
    ))
    public void listenDelete(Long spuId) {
        if (spuId == null) {
            return;
        }
        //处理消息，对索引库删除
        pageService.deleteHtml(spuId);
    }
}