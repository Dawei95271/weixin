package com.hotel.catering.modules.dish.service;

import com.hotel.catering.modules.dish.vo.DishCategoryVO;
import com.hotel.catering.modules.dish.vo.DishVO;
import java.util.List;

public interface DishService {

    List<DishCategoryVO> listCategories();

    List<DishVO> listDishes(Long categoryId);

    DishVO getDishDetail(Long id);
}
