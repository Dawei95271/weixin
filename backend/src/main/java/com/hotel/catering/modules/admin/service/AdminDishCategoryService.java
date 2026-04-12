package com.hotel.catering.modules.admin.service;

import com.hotel.catering.modules.admin.dto.AdminDishCategorySaveDTO;
import com.hotel.catering.modules.admin.dto.AdminDishCategoryStatusDTO;
import com.hotel.catering.modules.admin.vo.AdminDishCategoryVO;
import java.util.List;

public interface AdminDishCategoryService {

    List<AdminDishCategoryVO> list();

    AdminDishCategoryVO save(AdminDishCategorySaveDTO dto);

    AdminDishCategoryVO updateStatus(AdminDishCategoryStatusDTO dto);
}
