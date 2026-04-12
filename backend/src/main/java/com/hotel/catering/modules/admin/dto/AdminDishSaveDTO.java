package com.hotel.catering.modules.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class AdminDishSaveDTO {

    private Long id;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @NotBlank(message = "菜品名称不能为空")
    private String name;

    private String subtitle;

    private String description;

    @NotNull(message = "价格不能为空")
    private BigDecimal basePrice;

    private Integer supportsRoomDelivery;

    private Integer isRecommend;
}
