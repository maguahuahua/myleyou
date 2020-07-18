package com.leyou.item.pojo;

import com.sun.javafx.beans.IDProperty;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * @author chenxm
 * @date 2020/7/7 - 15:21
 */

@Data
@Table(name = "tb_spec_group")
public class SpecGroup {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long cid;
    private String name;


    //查详情时用，顺便把组下的参数查了
    @Transient
    private List<SpecParam> params;
}
