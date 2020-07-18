package com.leyou.search.mq;

import com.leyou.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author chenxm
 * @date 2020/7/17 - 11:32
 */

@Component
public class ItemListener {

    @Autowired
    private SearchService searchService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.item.insert.queue", durable = "true"),   //这个name随便起
            exchange = @Exchange(name = "ly.item.exchange", type = ExchangeTypes.TOPIC),  //name与配置文件的对应
            key = {"item.insert", "item.update"}                                          //匹配发的mq
    ))
    public void listenCreateOrUpdate(Long spuId) {
        if (spuId == null) {
            return;
        }
        //处理消息，对索引库新增或者修改。
        //异常等spring捕获处理消息，ack是默认打开的，异常不会发送ack  ？
        try {
            searchService.createOrUpdateIndex(spuId);
        } catch (Exception e) {
//这段代码表示，这次消息，我已经接受并消费掉了，不会再重复发送消费
            e.printStackTrace();
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.item.delete.queue", durable = "true"),   //这个name随便起
            exchange = @Exchange(name = "ly.item.exchange", type = ExchangeTypes.TOPIC),  //name与配置文件的对应
            key = {"item.delete"}                                          //匹配发的mq
    ))
    public void listenDelete(Long spuId) {
        if (spuId == null) {
            return;
        }
        //处理消息，对索引库删除
        try{

            searchService.deleteIndex(spuId);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
