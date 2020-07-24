package com.leyou.page.service;

import com.leyou.page.client.BrandClient;
import com.leyou.page.client.CategoryClient;
import com.leyou.page.client.GoodsClient;
import com.leyou.page.client.SpecificationClient;
import com.leyou.pojo.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author chenxm
 * @date 2020/7/14 - 19:27
 */

@Log4j2
@Service
public class PageService {
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private TemplateEngine templateEngine;


    public Map<String, Object> loadModel(Long spuId) {
        HashMap<String, Object> model = new HashMap<>();
        //查spu
        Spu spu = goodsClient.querySpuById(spuId);
        //查skus
        List<Sku> skus = spu.getSkus();
        //查询详情
        SpuDetail spuDetail = spu.getSpuDetail();
        //查询品牌
        Brand brand = brandClient.queryById(spu.getBrandId());
        //查询分类
        List<Category> categories = categoryClient.queryCategoryByIds(
                Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //查询规格参数
        List<SpecGroup> specGroups = specificationClient.queryGroupByCid2(spu.getCid3());

        model.put("title", spu.getTitle());
        model.put("subTitle", spu.getSubTitle());
        model.put("spu", spu);
        model.put("skus", skus);
        model.put("detail", spuDetail);
        model.put("brand", brand);
        model.put("categories", categories);
        model.put("specs", specGroups);

        return model;
    }

    //页面静态化thymeleaf技术
    public void createHtml(Long spuId) {
        //上下文
        Context context = new Context();
        context.setVariables(loadModel(spuId));
        //输出流
        File dest = new File("D:\\idea\\myfiles\\ly2\\staticHtml", spuId + ".html");

        if (dest.exists()) {
            dest.delete();
        }

        try (PrintWriter printWriter = new PrintWriter(dest, "UTF-8")) {   //这样好像自动释放流
            //生成HTML
            templateEngine.process("item", context, printWriter);
        } catch (Exception e) {
            log.error("[静态页服务] 生成静态页异常", e);
        }

    }


    public void deleteHtml(Long spuId) {
        File dest = new File("D:\\idea\\myfiles\\ly2\\staticHtml", spuId + ".html");
        if (dest.exists()) {
            dest.delete();
        }
    }
}
