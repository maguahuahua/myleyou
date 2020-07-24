package com.leyou.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chenxm
 * @date 2020/7/22 - 12:25
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {           //data To Object，前端发给后端的
    private Long skuId;  //
    private Integer num; //购买数量
}
