package com.hotel.catering.modules.admin.service;

import com.hotel.catering.modules.admin.dto.AdminBanquetFollowCreateDTO;
import com.hotel.catering.modules.admin.vo.AdminBanquetFollowVO;
import java.util.List;

public interface AdminBanquetFollowService {

    List<AdminBanquetFollowVO> list(Long reservationId);

    AdminBanquetFollowVO create(AdminBanquetFollowCreateDTO dto);
}
