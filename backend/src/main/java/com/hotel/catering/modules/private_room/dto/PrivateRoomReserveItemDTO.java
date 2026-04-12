package com.hotel.catering.modules.private_room.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrivateRoomReserveItemDTO {

    @NotNull(message = "菜品ID不能为空")
    private Long dishId;

    @Min(value = 1, message = "数量至少为1")
    private Integer quantity;
}
