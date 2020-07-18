package com.leyou.common.vo;

import com.leyou.common.enums.ExceptionEnum;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.concurrent.Executor;

/**
 * @author chenxm
 * @date 2020/6/30 - 17:21
 */

@Data
public class ExceptionResult {
    private int status;
    private String msg;
    private Long timestamp;

    //构造赋值,需要传的是枚举
    public ExceptionResult(ExceptionEnum em) {
        this.status = em.getCode();
        this.msg = em.getMsg();
        this.timestamp = System.currentTimeMillis();
    }
}
