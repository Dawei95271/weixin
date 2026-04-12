package com.hotel.catering.modules.admin.controller;

import com.hotel.catering.common.api.ApiResponse;
import com.hotel.catering.modules.admin.dto.AdminDishCategorySaveDTO;
import com.hotel.catering.modules.admin.dto.AdminDishCategoryStatusDTO;
import com.hotel.catering.modules.admin.service.AdminDishCategoryService;
import com.hotel.catering.modules.admin.vo.AdminDishCategoryVO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dish-category")
@RequiredArgsConstructor
public class AdminDishCategoryController {

    private final AdminDishCategoryService adminDishCategoryService;

    @GetMapping("/list")
    public ApiResponse<List<AdminDishCategoryVO>> list() {
        return ApiResponse.success(adminDishCategoryService.list());
    }

    @PostMapping("/save")
    public ApiResponse<AdminDishCategoryVO> save(@Valid @RequestBody AdminDishCategorySaveDTO dto) {
        return ApiResponse.success(adminDishCategoryService.save(dto));
    }

    @PostMapping("/status")
    public ApiResponse<AdminDishCategoryVO> updateStatus(@Valid @RequestBody AdminDishCategoryStatusDTO dto) {
        return ApiResponse.success(adminDishCategoryService.updateStatus(dto));
    }
}
