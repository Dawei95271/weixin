package com.hotel.catering.modules.order.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderVO {

    private Long id;
    private String orderNo;
    private String orderScene;
    private String contactName;
    private String contactPhone;
    private String roomNo;
    private String orderStatus;
    private BigDecimal payableAmount;
    private LocalDateTime createdAt;
    private List<OrderItemVO> items;
}
