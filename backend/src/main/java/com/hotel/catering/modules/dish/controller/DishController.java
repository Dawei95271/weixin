package com.hotel.catering.modules.dish.controller;

import com.hotel.catering.common.api.ApiResponse;
import com.hotel.catering.modules.dish.service.DishService;
import com.hotel.catering.modules.dish.vo.DishCategoryVO;
import com.hotel.catering.modules.dish.vo.DishVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/dish")
@RequiredArgsConstructor
public class DishController {

    private final DishService dishService;

    @GetMapping("/category/list")
    public ApiResponse<List<DishCategoryVO>> listCategories() {
        return ApiResponse.success(dishService.listCategories());
    }

    @GetMapping("/list")
    public ApiResponse<List<DishVO>> listDishes(@RequestParam(required = false) Long categoryId) {
        return ApiResponse.success(dishService.listDishes(categoryId));
    }

    @GetMapping("/detail/{id}")
    public ApiResponse<DishVO> getDishDetail(@PathVariable Long id) {
        return ApiResponse.success(dishService.getDishDetail(id));
    }
}
