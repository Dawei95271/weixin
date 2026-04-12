package com.hotel.catering.modules.admin.service;

import com.hotel.catering.modules.admin.dto.AdminDishSaveDTO;
import com.hotel.catering.modules.admin.dto.AdminDishStatusDTO;
import com.hotel.catering.modules.admin.vo.AdminDishVO;
import java.util.List;

public interface AdminDishService {

    List<AdminDishVO> list();

    AdminDishVO save(AdminDishSaveDTO dto);

    AdminDishVO updateStatus(AdminDishStatusDTO dto);
}
