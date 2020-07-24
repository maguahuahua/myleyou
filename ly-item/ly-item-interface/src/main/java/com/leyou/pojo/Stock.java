package com.leyou.pojo;

import lombok.Data;
import sun.util.resources.da.LocaleNames_da;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author chenxm
 * @date 2020/7/9 - 11:31
 */

@Data
@Table(name = "tb_stock")
public class Stock {
    @Id
    private Long skuId;
    private Integer seckillStock;   //秒杀可用库存
    private Integer seckillTotal;       //已秒杀数量
    private Integer stock;      //正常库存
}
