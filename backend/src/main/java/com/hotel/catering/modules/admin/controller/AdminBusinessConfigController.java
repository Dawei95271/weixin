package com.hotel.catering.modules.admin.controller;

import com.hotel.catering.common.api.ApiResponse;
import com.hotel.catering.modules.admin.dto.AdminBusinessConfigSaveDTO;
import com.hotel.catering.modules.admin.service.AdminBusinessConfigService;
import com.hotel.catering.modules.admin.vo.AdminBusinessConfigVO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/config")
@RequiredArgsConstructor
public class AdminBusinessConfigController {

    private final AdminBusinessConfigService adminBusinessConfigService;

    @GetMapping("/list")
    public ApiResponse<List<AdminBusinessConfigVO>> list() {
        return ApiResponse.success(adminBusinessConfigService.list());
    }

    @PostMapping("/save")
    public ApiResponse<List<AdminBusinessConfigVO>> save(@Valid @RequestBody AdminBusinessConfigSaveDTO dto) {
        return ApiResponse.success(adminBusinessConfigService.save(dto));
    }
}
