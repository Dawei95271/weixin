package com.hotel.catering.modules.dish.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import lombok.Data;

@Data
@TableName("dish")
public class Dish {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long categoryId;
    private String name;
    private String subtitle;
    private String coverImage;
    private String description;
    private BigDecimal basePrice;
    private Integer status;
    private Integer isRecommend;
    private Integer supportsRoomDelivery;
    private Integer sort;
}
