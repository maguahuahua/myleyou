package com.leyou.enums;

import lombok.Data;

/**
 * @author chenxm
 * @date 2020/7/22 - 19:06
 */


public enum OrderStatusEnum {

    INIT(1, "初始化，未付款"),
    PAY_UP(2, "已付款，未发货"),
    DELIVERED(3, "已发货，未确认"),
    CONFIRMED(4, "已确认,未评价"),
    CLOSED(5, "已关闭"),
    RATED(6, "已评价，交易结束")
    ;

    private int code;
    private String desc;

    OrderStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public Integer value(){
        return this.code;
    }
}
