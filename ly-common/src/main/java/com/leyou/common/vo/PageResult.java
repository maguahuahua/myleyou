package com.leyou.common.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.aop.target.LazyInitTargetSource;

import java.util.List;

/**
 * @author chenxm
 * @date 2020/7/3 - 20:19
 */



@Data
public class PageResult <T>{
    private Long total;   //总条数
    private Integer totalPage;
    private List<T> items;  //当前页数据

    public PageResult() {
    }

    public PageResult(Long total, Integer totalPage, List<T> items) {
        this.total = total;
        this.totalPage = totalPage;
        this.items = items;
    }

    public PageResult(Long total, List<T> items) {
        this.total = total;
        this.items = items;
    }
}
