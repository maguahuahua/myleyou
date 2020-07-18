package com.leyou.common.exception;

import com.leyou.common.enums.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author chenxm
 * @date 2020/6/30 - 16:42
 * 自定义异常类
 */

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LyException extends RuntimeException {
    private ExceptionEnum exceptionEnum;

}
