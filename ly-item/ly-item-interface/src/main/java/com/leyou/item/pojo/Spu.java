package com.leyou.item.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Time;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @author chenxm
 * @date 2020/7/7 - 20:52
 *
 */
@Data
@Table(name = "tb_spu")
public class Spu {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long brandId;
    private Long cid1;     //分类id
    private Long cid2;
    private Long cid3;
    private String title;  //标题
    private String subTitle;  //子标题
    private Boolean saleable;  //是否上架
    @JsonIgnore
    private Boolean valid;  //是否有效，逻辑删除用
    private Date create_time;
    @JsonIgnore        //不返回此字段json
    private Date last_update_time;


    /**
     * 上面部分算是po，跟数据库字段一一对应的
     *  实际开发中要用 vo , 新建一个对象，再加下面的字段（用于显示），或者删除某些字段
     *  中间要转换，怎么转换要取学习下
     */
    @Transient         //通用mapper不把这字段当做数据库字段
    private String bname;
    @Transient
    private String cname;

    //下面两个是新增商品用到的,传来的json有下面这些字段
    @Transient
    private List<Sku> skus;
    @Transient
    private SpuDetail spuDetail;
}
