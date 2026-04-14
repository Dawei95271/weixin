package com.hotel.catering.modules.dish.vo;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DishVO {

    private Long id;
    private Long categoryId;
    private String name;
    private String subtitle;
    private String description;
    private String coverImage;
    private BigDecimal price;
    private Integer isRecommend;
    private Integer supportsRoomDelivery;
}
