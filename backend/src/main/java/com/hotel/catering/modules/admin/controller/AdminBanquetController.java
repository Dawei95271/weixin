package com.hotel.catering.modules.admin.controller;

import com.hotel.catering.common.api.ApiResponse;
import com.hotel.catering.modules.admin.dto.AdminBanquetStatusUpdateDTO;
import com.hotel.catering.modules.admin.service.AdminManagementService;
import com.hotel.catering.modules.banquet.vo.BanquetReservationVO;
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
@RequestMapping("/admin/banquet")
@RequiredArgsConstructor
public class AdminBanquetController {

    private final AdminManagementService adminManagementService;

    @GetMapping("/list")
    public ApiResponse<List<BanquetReservationVO>> list() {
        return ApiResponse.success(adminManagementService.listBanquetReservations());
    }

    @GetMapping("/detail/{id}")
    public ApiResponse<BanquetReservationVO> detail(@PathVariable Long id) {
        return ApiResponse.success(adminManagementService.getBanquetReservationDetail(id));
    }

    @PostMapping("/status")
    public ApiResponse<BanquetReservationVO> updateStatus(@Valid @RequestBody AdminBanquetStatusUpdateDTO dto) {
        return ApiResponse.success(adminManagementService.updateBanquetReservationStatus(dto));
    }
}
