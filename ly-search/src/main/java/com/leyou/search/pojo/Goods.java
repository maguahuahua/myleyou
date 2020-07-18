package com.leyou.search.pojo;

import lombok.Data;
import org.bouncycastle.operator.KeyWrapper;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author chenxm
 * @date 2020/7/11 - 16:30
 */
@Data
@Document(indexName = "goods", type = "docs", shards = 1, replicas = 1)
public class Goods {
    @Id
    private Long id;   //spuid

    @Field(type = FieldType.Text, analyzer = "ik_max_word")         //@Field 应该是能添加到索引库的
    private String all;//所有需要被搜索的信息，包含标题、分类、品牌……

    @Field(type = FieldType.Keyword, index = false)
    private String subTitle;  //卖点

    private Long brandId;
    private Long cid1;
    private Long cid2;
    private Long cid3;
    private Date createTime;   //spu创建时间
    private Set<Long> price;

    @Field(type = FieldType.Keyword, index = false)
    private String skus;  //sku信息的json结构，不需要被搜索

    private Map<String, Object> specs;  //可搜索的规格参数，键是参数名，值是参数值
    //  {"机身内存" : "4GB"}         specs.机身内存.keyword:……
}
