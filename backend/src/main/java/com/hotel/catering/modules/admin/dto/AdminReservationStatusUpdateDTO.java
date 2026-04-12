package com.hotel.catering.modules.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminReservationStatusUpdateDTO {

    @NotNull(message = "预约ID不能为空")
    private Long reservationId;

    @NotBlank(message = "预约状态不能为空")
    private String reservationStatus;
}
