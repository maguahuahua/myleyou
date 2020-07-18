package com.leyou.common.advice;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.ExceptionResult;
import org.apache.catalina.startup.EngineRuleSet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author chenxm
 * @date 2020/6/30 - 16:37
 * <p>
 * 异常处理类
 *
 * new 自定义异常（枚举（code，msg）），传一个枚举;
 * 抛给处理类处理，判断是自定义异常类，走对应方法，方法参数得到这个异常，处理及返回封装对象
 * （返回的对象又可以自定义，这里是自定义的构造方法）
 */

@ControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(LyException.class)
    public ResponseEntity<ExceptionResult> handlerException(LyException e) {  //传进来自定义异常
        return ResponseEntity.status(e.getExceptionEnum().getCode())
                .body(new ExceptionResult(e.getExceptionEnum()));
//        .body(new ExceptionResult(ExceptionEnum.BRAND_NOT_FOUND));
    }
}
