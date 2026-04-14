package com.hotel.catering.modules.home.controller;

import com.hotel.catering.common.api.ApiResponse;
import com.hotel.catering.modules.system.service.BusinessConfigService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.LinkedHashMap;
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
    private final ObjectMapper objectMapper;

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("ok");
    }

    @GetMapping("/home/index")
    public ApiResponse<Map<String, Object>> index() {
        Map<String, String> configs = businessConfigService.getPublicConfigs();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("projectName", "酒店二楼餐饮服务小程序");
        result.put("businessScopes", List.of("点餐", "客房送餐", "包间预约", "宴席预约"));
        result.put("contactPhone", configs.getOrDefault("CONTACT_PHONE", ""));
        result.put("deliveryFee", configs.getOrDefault("DELIVERY_FEE", "0"));
        result.put("minOrderAmount", configs.getOrDefault("MIN_ORDER_AMOUNT", "0"));
        result.put("breakfastHours", configs.getOrDefault("BREAKFAST_HOURS", ""));
        result.put("lunchHours", configs.getOrDefault("LUNCH_HOURS", ""));
        result.put("dinnerHours", configs.getOrDefault("DINNER_HOURS", ""));
        result.put("homeNotice", configs.getOrDefault("HOME_NOTICE", ""));
        result.put("roomDeliveryNotice", configs.getOrDefault("ROOM_DELIVERY_NOTICE", ""));
        result.put("homeBanners", parseBanners(configs.get("HOME_BANNERS")));
        return ApiResponse.success(result);
    }

    private List<Map<String, String>> parseBanners(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return defaultBanners();
        }
        try {
            return objectMapper.readValue(rawValue, new TypeReference<List<Map<String, String>>>() {
            });
        } catch (Exception ignored) {
            return defaultBanners();
        }
    }

    private List<Map<String, String>> defaultBanners() {
        return List.of(
            Map.of(
                "title", "客房扫码点餐",
                "subtitle", "扫码识别房间后即可享受二楼送餐服务",
                "linkType", "ROOM",
                "linkValue", "",
                "tone", "amber"
            ),
            Map.of(
                "title", "包间提前预约",
                "subtitle", "十个包间可选，早餐中餐晚餐均可预约",
                "linkType", "PRIVATE_ROOM",
                "linkValue", "",
                "tone", "tea"
            ),
            Map.of(
                "title", "婚宴寿宴预约",
                "subtitle", "线上留资，营业时间内优先电话跟进",
                "linkType", "BANQUET",
                "linkValue", "",
                "tone", "copper"
            )
        );
    }
}
