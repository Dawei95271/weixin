package com.hotel.catering.modules.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminDishCategoryStatusDTO {

    @NotNull(message = "分类ID不能为空")
    private Long id;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
