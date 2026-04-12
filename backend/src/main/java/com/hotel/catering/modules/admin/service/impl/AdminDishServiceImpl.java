package com.hotel.catering.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hotel.catering.common.exception.BusinessException;
import com.hotel.catering.modules.admin.dto.AdminDishSaveDTO;
import com.hotel.catering.modules.admin.dto.AdminDishStatusDTO;
import com.hotel.catering.modules.admin.service.AdminDishService;
import com.hotel.catering.modules.admin.vo.AdminDishVO;
import com.hotel.catering.modules.dish.entity.Dish;
import com.hotel.catering.modules.dish.entity.DishCategory;
import com.hotel.catering.modules.dish.mapper.DishCategoryMapper;
import com.hotel.catering.modules.dish.mapper.DishMapper;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminDishServiceImpl implements AdminDishService {

    private final DishMapper dishMapper;
    private final DishCategoryMapper dishCategoryMapper;

    @Override
    public List<AdminDishVO> list() {
        Map<Long, String> categoryMap = dishCategoryMapper.selectList(new LambdaQueryWrapper<DishCategory>())
            .stream()
            .collect(Collectors.toMap(DishCategory::getId, DishCategory::getName));
        return dishMapper.selectList(new LambdaQueryWrapper<Dish>()
                .orderByAsc(Dish::getCategoryId, Dish::getSort, Dish::getId))
            .stream()
            .map(dish -> toVO(dish, categoryMap.getOrDefault(dish.getCategoryId(), "")))
            .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminDishVO save(AdminDishSaveDTO dto) {
        Dish dish = dto.getId() == null ? new Dish() : dishMapper.selectById(dto.getId());
        if (dish == null) {
            throw new BusinessException("菜品不存在");
        }
        DishCategory category = dishCategoryMapper.selectById(dto.getCategoryId());
        if (category == null) {
            throw new BusinessException("菜品分类不存在");
        }
        dish.setCategoryId(dto.getCategoryId());
        dish.setName(dto.getName());
        dish.setSubtitle(dto.getSubtitle() == null ? "" : dto.getSubtitle());
        dish.setDescription(dto.getDescription() == null ? "" : dto.getDescription());
        dish.setBasePrice(dto.getBasePrice());
        dish.setSupportsRoomDelivery(dto.getSupportsRoomDelivery() == null ? 1 : dto.getSupportsRoomDelivery());
        dish.setIsRecommend(dto.getIsRecommend() == null ? 0 : dto.getIsRecommend());
        if (dish.getStatus() == null) {
            dish.setStatus(1);
        }
        if (dish.getSort() == null) {
            dish.setSort(0);
        }
        if (dish.getCoverImage() == null) {
            dish.setCoverImage("");
        }
        if (dto.getId() == null) {
            dishMapper.insert(dish);
        } else {
            dishMapper.updateById(dish);
        }
        return toVO(dish, category.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminDishVO updateStatus(AdminDishStatusDTO dto) {
        Dish dish = dishMapper.selectById(dto.getId());
        if (dish == null) {
            throw new BusinessException("菜品不存在");
        }
        dish.setStatus(dto.getStatus());
        dishMapper.updateById(dish);
        DishCategory category = dishCategoryMapper.selectById(dish.getCategoryId());
        return toVO(dish, category == null ? "" : category.getName());
    }

    private AdminDishVO toVO(Dish dish, String categoryName) {
        return new AdminDishVO(
            dish.getId(),
            dish.getCategoryId(),
            categoryName,
            dish.getName(),
            dish.getSubtitle(),
            dish.getDescription(),
            dish.getBasePrice(),
            dish.getStatus(),
            dish.getSupportsRoomDelivery(),
            dish.getIsRecommend()
        );
    }
}
