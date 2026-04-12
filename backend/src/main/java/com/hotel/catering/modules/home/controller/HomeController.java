package com.hotel.catering.modules.home.controller;

import com.hotel.catering.common.api.ApiResponse;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HomeController {

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("ok");
    }

    @GetMapping("/home/index")
    public ApiResponse<Map<String, Object>> index() {
        return ApiResponse.success(Map.of(
            "projectName", "酒店二楼餐饮服务小程序",
            "businessScopes", List.of("点餐", "客房送餐", "包间预约", "宴席预约")
        ));
    }
}
