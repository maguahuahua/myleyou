package com.leyou.item.service;

import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.junit.Test;
import com.leyou.item.service.GoodsService;
import com.leyou.item.service.impl.GoodsServiceImpl;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @author chenxm
 * @date 2020/7/17 - 15:47
 */

/*

@SpringBootTest
@RunWith(SpringRunner.class)
public class Test1 {
    @Autowired
    private GoodsService goodsService;

    @Test
    public void test(){
        Spu spu = new Spu();
        spu.setId(196L);

        spu.setCid1(74L);
        spu.setCid2(75L);
        spu.setCid3(76L);
        spu.setBrandId(8557L);
        spu.setTitle("测试");
        spu.setCreate_time(new Date());
        spu.setLast_update_time(spu.getCreate_time());
        spu.setSaleable(true);
        spu.setValid(false);
        SpuDetail spuDetail = new SpuDetail();
        spu.setSpuDetail(spuDetail);
        spuDetail.setSpecialSpec("11");
        spuDetail.setGenericSpec("22");
        goodsService.saveGoods(spu);
    }
}
*/
