package com.hotel.catering.modules.admin.vo;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminDishVO {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String subtitle;
    private String description;
    private BigDecimal basePrice;
    private Integer status;
    private Integer supportsRoomDelivery;
    private Integer isRecommend;
}
