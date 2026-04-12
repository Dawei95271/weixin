package com.hotel.catering.modules.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminDishStatusDTO {

    @NotNull(message = "菜品ID不能为空")
    private Long id;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
