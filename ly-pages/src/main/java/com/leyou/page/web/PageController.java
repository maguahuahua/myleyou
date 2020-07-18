package com.leyou.page.web;

import com.leyou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @author chenxm
 * @date 2020/7/14 - 19:17
 */

@Controller
public class PageController {
    @Autowired
    private PageService pageService;

    @GetMapping("item/{id}.html")
    public String toItemPage(@PathVariable("id") Long spuId, Model model) {
        System.out.println("到了");
        //查询模型数据
        Map<String, Object> attributes = pageService.loadModel(spuId);
        //准备模型数据
        model.addAllAttributes(attributes);
        //返回视图
        System.out.println("之前");
        return "item";
    }
}
