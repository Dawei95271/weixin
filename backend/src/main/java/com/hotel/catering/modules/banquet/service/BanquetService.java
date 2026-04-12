package com.hotel.catering.modules.banquet.service;

import com.hotel.catering.modules.banquet.dto.BanquetReservationCreateDTO;
import com.hotel.catering.modules.banquet.vo.BanquetReservationVO;
import java.util.List;

public interface BanquetService {

    BanquetReservationVO create(BanquetReservationCreateDTO dto);

    List<BanquetReservationVO> list();

    BanquetReservationVO detail(Long id);
}
