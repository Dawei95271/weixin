package com.hotel.catering.modules.system.controller;

import com.hotel.catering.common.api.ApiResponse;
import com.hotel.catering.modules.system.service.BusinessConfigService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class PublicBusinessConfigController {

    private final BusinessConfigService businessConfigService;

    @GetMapping("/public")
    public ApiResponse<Map<String, String>> publicConfigs() {
        return ApiResponse.success(businessConfigService.getPublicConfigs());
    }
}
