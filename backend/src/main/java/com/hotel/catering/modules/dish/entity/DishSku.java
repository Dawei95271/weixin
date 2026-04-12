package com.hotel.catering.modules.dish.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import lombok.Data;

@Data
@TableName("dish_sku")
public class DishSku {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long dishId;
    private String skuName;
    private BigDecimal price;
    private Integer stock;
    private Integer status;
    private Integer sort;
}
