package com.leyou.search.repository;

import com.leyou.common.vo.PageResult;
import com.leyou.pojo.Spu;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chenxm
 * @date 2020/7/11 - 20:39
 */


//创建索引库

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsRepositoryTest {


    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private ElasticsearchTemplate template;
    @Autowired
    private SearchService searchService;
    @Autowired
    private GoodsClient goodsClient;

    @Test
    public void testCreateIndex() {
        template.createIndex(Goods.class);
        template.putMapping(Goods.class);
    }


    @Test
    public void loadData() {
        int page = 1;
        int rows = 100;
        int size = 0;
        do {
            //查spu信息
            PageResult<Spu> result = goodsClient.querySpuByPage(page, rows, true, null);
            List<Spu> spuList = result.getItems();
            if (CollectionUtils.isEmpty(spuList)) {
                break;
            }
            //构建goods
            List<Goods> goodsList = spuList.stream().map(searchService::buildGoods).collect(Collectors.toList());
            //存入所以库
            goodsRepository.saveAll(goodsList);
            //翻页
            page++;
            size = spuList.size();
        } while (size == 100);
    }

}