package com.leyou.page;

import com.leyou.page.service.PageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author chenxm
 * @date 2020/7/15 - 14:50
 */


@SpringBootTest
@RunWith(SpringRunner.class)
public class Pagetest {
    @Autowired
    private PageService pageService;



    /**
     * thymeleaf静态化
     * 静态页面测试，生成html到本机，要自己传到linux的/usr/local/nginx/html/item下，
     * 这样访问商品详情页面不经过后台代码了，即使关闭ly-page服务，也能访问，
     * 提高并发
     */

    @Test
    public void createHtml() {
        pageService.createHtml(141L);
    }
}
