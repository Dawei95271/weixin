package com.hotel.catering.modules.home.controller;

import com.hotel.catering.common.api.ApiResponse;
import com.hotel.catering.modules.system.service.BusinessConfigService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HomeController {

    private final BusinessConfigService businessConfigService;

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("ok");
    }

    @GetMapping("/home/index")
    public ApiResponse<Map<String, Object>> index() {
        Map<String, String> configs = businessConfigService.getPublicConfigs();
        return ApiResponse.success(Map.of(
            "projectName", "酒店二楼餐饮服务小程序",
            "businessScopes", List.of("点餐", "客房送餐", "包间预约", "宴席预约"),
            "contactPhone", configs.getOrDefault("CONTACT_PHONE", ""),
            "deliveryFee", configs.getOrDefault("DELIVERY_FEE", "0"),
            "minOrderAmount", configs.getOrDefault("MIN_ORDER_AMOUNT", "0"),
            "breakfastHours", configs.getOrDefault("BREAKFAST_HOURS", ""),
            "lunchHours", configs.getOrDefault("LUNCH_HOURS", ""),
            "dinnerHours", configs.getOrDefault("DINNER_HOURS", ""),
            "homeNotice", configs.getOrDefault("HOME_NOTICE", ""),
            "roomDeliveryNotice", configs.getOrDefault("ROOM_DELIVERY_NOTICE", "")
        ));
    }
}
