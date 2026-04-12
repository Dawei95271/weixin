package com.hotel.catering.modules.admin.service;

import com.hotel.catering.modules.admin.dto.AdminLoginDTO;
import com.hotel.catering.modules.admin.vo.AdminLoginVO;

public interface AdminAuthService {

    AdminLoginVO login(AdminLoginDTO dto);
}
