package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * @author chenxm
 * @date 2020/7/9 - 11:37
 */

@Data
@Table(name = "tb_sku")
public class Sku {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long spuId;
    private String title;
    private String images;
    private Long price;
    private String ownSpec;   //商品特殊规格键值对，如4G、机身具体颜色
    private String indexes;   //商品特殊规格下标
    private Boolean enable;     //是否有效，逻辑删除
    private Date createTime;       //创建时间
    private Date lastUpdateTime;

    @Transient
    private Integer stock;      //库存,传过来的数据中有stock
}
