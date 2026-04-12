package com.hotel.catering.modules.admin.controller;

import com.hotel.catering.common.api.ApiResponse;
import com.hotel.catering.modules.admin.dto.AdminOrderStatusUpdateDTO;
import com.hotel.catering.modules.admin.service.AdminManagementService;
import com.hotel.catering.modules.order.vo.OrderVO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminManagementService adminManagementService;

    @GetMapping("/list")
    public ApiResponse<List<OrderVO>> list(
        @RequestParam(required = false) String orderStatus,
        @RequestParam(required = false) String orderScene
    ) {
        return ApiResponse.success(adminManagementService.listOrders(orderStatus, orderScene));
    }

    @GetMapping("/detail/{id}")
    public ApiResponse<OrderVO> detail(@PathVariable Long id) {
        return ApiResponse.success(adminManagementService.getOrderDetail(id));
    }

    @PostMapping("/status")
    public ApiResponse<OrderVO> updateStatus(@Valid @RequestBody AdminOrderStatusUpdateDTO dto) {
        return ApiResponse.success(adminManagementService.updateOrderStatus(dto));
    }
}
