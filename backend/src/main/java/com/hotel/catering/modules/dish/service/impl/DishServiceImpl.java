package com.hotel.catering.modules.dish.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hotel.catering.common.exception.BusinessException;
import com.hotel.catering.modules.dish.entity.Dish;
import com.hotel.catering.modules.dish.entity.DishCategory;
import com.hotel.catering.modules.dish.mapper.DishCategoryMapper;
import com.hotel.catering.modules.dish.mapper.DishMapper;
import com.hotel.catering.modules.dish.service.DishService;
import com.hotel.catering.modules.dish.vo.DishCategoryVO;
import com.hotel.catering.modules.dish.vo.DishVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {

    private final DishCategoryMapper dishCategoryMapper;
    private final DishMapper dishMapper;

    @Override
    public List<DishCategoryVO> listCategories() {
        return dishCategoryMapper.selectList(new LambdaQueryWrapper<DishCategory>()
                .eq(DishCategory::getStatus, 1)
                .orderByAsc(DishCategory::getSort, DishCategory::getId))
            .stream()
            .map(item -> new DishCategoryVO(item.getId(), item.getName()))
            .toList();
    }

    @Override
    public List<DishVO> listDishes(Long categoryId) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<Dish>()
            .eq(Dish::getStatus, 1)
            .orderByAsc(Dish::getSort, Dish::getId);
        if (categoryId != null) {
            queryWrapper.eq(Dish::getCategoryId, categoryId);
        }
        return dishMapper.selectList(queryWrapper).stream()
            .map(item -> new DishVO(
                item.getId(),
                item.getCategoryId(),
                item.getName(),
                item.getSubtitle(),
                item.getBasePrice(),
                item.getSupportsRoomDelivery() != null && item.getSupportsRoomDelivery() == 1
            ))
            .toList();
    }

    @Override
    public DishVO getDishDetail(Long id) {
        if (id == null || id <= 0L) {
            throw new BusinessException("菜品不存在");
        }
        Dish dish = dishMapper.selectById(id);
        if (dish == null || dish.getStatus() == null || dish.getStatus() != 1) {
            throw new BusinessException("菜品不存在");
        }
        return new DishVO(
            dish.getId(),
            dish.getCategoryId(),
            dish.getName(),
            dish.getSubtitle(),
            dish.getBasePrice(),
            dish.getSupportsRoomDelivery() != null && dish.getSupportsRoomDelivery() == 1
        );
    }
}
