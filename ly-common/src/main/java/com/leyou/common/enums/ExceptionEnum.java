package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author chenxm
 * @date 2020/6/30 - 16:43
 * 传给自定义异常类
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ExceptionEnum {

    //枚举的构造方法简写，默认是私有的
    BRAND_NOT_FOUND(400, "品牌未找到"),
    GOODS_NOT_FOUND(400, "商品未找到"),
    GOODS_SAVE_ERROR(500, "商品新增失败"),
    GOODS_DETAIL_NOT_FOUND(400, "商品详情未找到"),
    GOODS_SKU_NOT_FOUND(400, "商品sku未找到"),
    GOOS_STOCK_NOT_FOUND(400, "商品stock未找到"),
    INVALID_USER_DATA_TYPE(400, "用户数据类型错误"),
    CATEGORY_NOT_FOUND(404, "商品分类没找到"),
    SPEC_GROUP_NOT_FOUND(404, "商品规格组不存在"),
    SPEC_PARAM_NOT_FOUND(404, "商品参数不存在"),
    BRAND_SAVE_ERROR(500, "新增品牌失败"),
    UPLOAD_FILE_ERROR(500, "文件上传失败"),
    INVALID_FILE_TYPE(400, "无效的文件类型"),
    UNAUTHORIZED(403, "未授权");

    private int code;
    private String msg;

}
