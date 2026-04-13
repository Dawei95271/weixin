package com.hotel.catering.modules.admin.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
public class AdminBusinessConfigSaveDTO {

    @Valid
    @NotEmpty(message = "配置项不能为空")
    private List<AdminBusinessConfigSaveItemDTO> items;
}
