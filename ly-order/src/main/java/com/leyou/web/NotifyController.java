package com.leyou.web;

import com.leyou.service.OrderService;
import com.sun.org.apache.regexp.internal.RE;
import net.sf.jsqlparser.statement.create.view.CreateView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chenxm
 * @date 2020/7/23 - 17:00
 */

@RestController
@RequestMapping("notify")
public class NotifyController {
    @Autowired
    private OrderService orderService;

    //微信返回的是xml。引入了xml的依赖，spring会对xml反序列化（而不是默认的json）

    @PostMapping(value = "pay", produces = "application/xml")
    public Map<String, String> feedback(@RequestBody Map<String, String> result) {
        //处理回调
        orderService.handleNotify(result);

        //返回成功
        Map<String, String> msg = new HashMap<>();
        msg.put("return_code", "SUCCESS");      //参考api
        msg.put("return_msg", "OK");
        return msg;

    }
}
