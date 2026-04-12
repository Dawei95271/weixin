package com.hotel.catering.modules.order.controller;

import com.hotel.catering.common.api.ApiResponse;
import com.hotel.catering.modules.order.dto.OrderSubmitDTO;
import com.hotel.catering.modules.order.service.OrderService;
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
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/submit")
    public ApiResponse<OrderVO> submit(@Valid @RequestBody OrderSubmitDTO dto) {
        return ApiResponse.success(orderService.submit(dto));
    }

    @GetMapping("/list")
    public ApiResponse<List<OrderVO>> list(@RequestParam(defaultValue = "1") Long userId) {
        return ApiResponse.success(orderService.listByUserId(userId));
    }

    @GetMapping("/detail/{id}")
    public ApiResponse<OrderVO> detail(@PathVariable Long id) {
        return ApiResponse.success(orderService.getDetail(id));
    }
}
