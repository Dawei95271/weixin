package com.hotel.catering.modules.admin.service;

import com.hotel.catering.modules.admin.dto.AdminBusinessConfigSaveDTO;
import com.hotel.catering.modules.admin.vo.AdminBusinessConfigVO;
import java.util.List;

public interface AdminBusinessConfigService {

    List<AdminBusinessConfigVO> list();

    List<AdminBusinessConfigVO> save(AdminBusinessConfigSaveDTO dto);
}
