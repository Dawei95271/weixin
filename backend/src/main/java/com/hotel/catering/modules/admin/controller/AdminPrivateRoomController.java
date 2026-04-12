package com.hotel.catering.modules.admin.controller;

import com.hotel.catering.common.api.ApiResponse;
import com.hotel.catering.modules.admin.dto.AdminReservationStatusUpdateDTO;
import com.hotel.catering.modules.admin.service.AdminManagementService;
import com.hotel.catering.modules.private_room.vo.PrivateRoomReservationVO;
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
@RequestMapping("/admin/private-room")
@RequiredArgsConstructor
public class AdminPrivateRoomController {

    private final AdminManagementService adminManagementService;

    @GetMapping("/reservation/list")
    public ApiResponse<List<PrivateRoomReservationVO>> listReservations() {
        return ApiResponse.success(adminManagementService.listPrivateRoomReservations());
    }

    @GetMapping("/reservation/detail/{id}")
    public ApiResponse<PrivateRoomReservationVO> detail(@PathVariable Long id) {
        return ApiResponse.success(adminManagementService.getPrivateRoomReservationDetail(id));
    }

    @PostMapping("/reservation/status")
    public ApiResponse<PrivateRoomReservationVO> updateStatus(@Valid @RequestBody AdminReservationStatusUpdateDTO dto) {
        return ApiResponse.success(adminManagementService.updatePrivateRoomReservationStatus(dto));
    }
}
