package com.hotel.catering.modules.admin.controller;

import com.hotel.catering.common.api.ApiResponse;
import com.hotel.catering.modules.admin.dto.AdminDishSaveDTO;
import com.hotel.catering.modules.admin.dto.AdminDishStatusDTO;
import com.hotel.catering.modules.admin.service.AdminDishService;
import com.hotel.catering.modules.admin.vo.AdminDishVO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dish")
@RequiredArgsConstructor
public class AdminDishController {

    private final AdminDishService adminDishService;

    @GetMapping("/list")
    public ApiResponse<List<AdminDishVO>> list() {
        return ApiResponse.success(adminDishService.list());
    }

    @PostMapping("/save")
    public ApiResponse<AdminDishVO> save(@Valid @RequestBody AdminDishSaveDTO dto) {
        return ApiResponse.success(adminDishService.save(dto));
    }

    @PostMapping("/status")
    public ApiResponse<AdminDishVO> updateStatus(@Valid @RequestBody AdminDishStatusDTO dto) {
        return ApiResponse.success(adminDishService.updateStatus(dto));
    }
}
