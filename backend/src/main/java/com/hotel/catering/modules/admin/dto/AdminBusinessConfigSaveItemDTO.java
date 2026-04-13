package com.hotel.catering.modules.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminBusinessConfigSaveItemDTO {

    @NotBlank(message = "配置键不能为空")
    private String configKey;

    @NotBlank(message = "配置名称不能为空")
    private String configName;

    private String configValue;
}
