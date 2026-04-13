package com.hotel.catering.modules.admin.vo;

public record AdminBusinessConfigVO(
    Long id,
    String configKey,
    String configName,
    String configValue
) {
}
