package com.leyou.search.repository;

import com.leyou.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author chenxm
 * @date 2020/7/11 - 20:37
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods, Long> {
}
