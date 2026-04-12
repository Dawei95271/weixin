package com.hotel.catering.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hotel.catering.common.exception.BusinessException;
import com.hotel.catering.modules.admin.dto.AdminDishCategorySaveDTO;
import com.hotel.catering.modules.admin.dto.AdminDishCategoryStatusDTO;
import com.hotel.catering.modules.admin.service.AdminDishCategoryService;
import com.hotel.catering.modules.admin.vo.AdminDishCategoryVO;
import com.hotel.catering.modules.dish.entity.DishCategory;
import com.hotel.catering.modules.dish.mapper.DishCategoryMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminDishCategoryServiceImpl implements AdminDishCategoryService {

    private final DishCategoryMapper dishCategoryMapper;

    @Override
    public List<AdminDishCategoryVO> list() {
        return dishCategoryMapper.selectList(new LambdaQueryWrapper<DishCategory>()
                .orderByAsc(DishCategory::getSort, DishCategory::getId))
            .stream()
            .map(item -> new AdminDishCategoryVO(item.getId(), item.getName(), item.getSort(), item.getStatus()))
            .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminDishCategoryVO save(AdminDishCategorySaveDTO dto) {
        DishCategory category = dto.getId() == null ? new DishCategory() : dishCategoryMapper.selectById(dto.getId());
        if (category == null) {
            throw new BusinessException("菜品分类不存在");
        }
        category.setName(dto.getName());
        category.setSort(dto.getSort() == null ? 0 : dto.getSort());
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        if (dto.getId() == null) {
            dishCategoryMapper.insert(category);
        } else {
            dishCategoryMapper.updateById(category);
        }
        return new AdminDishCategoryVO(category.getId(), category.getName(), category.getSort(), category.getStatus());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminDishCategoryVO updateStatus(AdminDishCategoryStatusDTO dto) {
        DishCategory category = dishCategoryMapper.selectById(dto.getId());
        if (category == null) {
            throw new BusinessException("菜品分类不存在");
        }
        category.setStatus(dto.getStatus());
        dishCategoryMapper.updateById(category);
        return new AdminDishCategoryVO(category.getId(), category.getName(), category.getSort(), category.getStatus());
    }
}
