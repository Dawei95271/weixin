package com.hotel.catering.modules.admin.controller;

import com.hotel.catering.common.api.ApiResponse;
import com.hotel.catering.modules.admin.dto.AdminBanquetFollowCreateDTO;
import com.hotel.catering.modules.admin.service.AdminBanquetFollowService;
import com.hotel.catering.modules.admin.vo.AdminBanquetFollowVO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/banquet/follow")
@RequiredArgsConstructor
public class AdminBanquetFollowController {

    private final AdminBanquetFollowService adminBanquetFollowService;

    @GetMapping("/list/{reservationId}")
    public ApiResponse<List<AdminBanquetFollowVO>> list(@PathVariable Long reservationId) {
        return ApiResponse.success(adminBanquetFollowService.list(reservationId));
    }

    @PostMapping("/create")
    public ApiResponse<AdminBanquetFollowVO> create(@Valid @RequestBody AdminBanquetFollowCreateDTO dto) {
        return ApiResponse.success(adminBanquetFollowService.create(dto));
    }
}
