package com.hotel.catering.modules.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminBanquetStatusUpdateDTO {

    @NotNull(message = "宴席预约ID不能为空")
    private Long reservationId;

    @NotBlank(message = "宴席状态不能为空")
    private String status;
}
