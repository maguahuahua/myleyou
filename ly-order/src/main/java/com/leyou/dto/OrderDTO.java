package com.leyou.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author chenxm
 * @date 2020/7/22 - 12:28
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    @NotNull
    private Long addressId;   //收货人地址id
    @NotNull
    private Integer paymentType;  //付款类型

    @NotNull
    private List<CartDTO> carts;  //订单详情

}
