package com.hotel.catering.modules.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminBanquetFollowCreateDTO {

    @NotNull(message = "宴席预约ID不能为空")
    private Long reservationId;

    @NotBlank(message = "跟进内容不能为空")
    private String followContent;

    private String nextFollowTime;
}
