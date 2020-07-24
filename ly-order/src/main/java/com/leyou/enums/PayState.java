package com.leyou.enums;

/**
 * @author chenxm
 * @date 2020/7/24 - 8:44
 */
public enum PayState {

    NOT_PAY(0),
    SUCCESS(1),
    FAIL(2);

    int value;

    PayState(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }

}
