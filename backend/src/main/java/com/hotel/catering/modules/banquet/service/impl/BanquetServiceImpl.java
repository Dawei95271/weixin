package com.hotel.catering.modules.banquet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hotel.catering.common.util.NoGenerator;
import com.hotel.catering.modules.banquet.dto.BanquetReservationCreateDTO;
import com.hotel.catering.modules.banquet.entity.BanquetReservation;
import com.hotel.catering.modules.banquet.mapper.BanquetReservationMapper;
import com.hotel.catering.modules.banquet.service.BanquetService;
import com.hotel.catering.modules.banquet.vo.BanquetReservationVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BanquetServiceImpl implements BanquetService {

    private final BanquetReservationMapper banquetReservationMapper;

    @Override
    public BanquetReservationVO create(BanquetReservationCreateDTO dto) {
        BanquetReservation reservation = new BanquetReservation();
        reservation.setReservationNo(NoGenerator.next("BQ"));
        reservation.setUserId(1L);
        reservation.setBanquetType(dto.getBanquetType());
        reservation.setReserveDate(dto.getReserveDate());
        reservation.setGuestCount(dto.getGuestCount());
        reservation.setBudgetAmount(dto.getBudgetAmount());
        reservation.setContactName(dto.getContactName());
        reservation.setContactPhone(dto.getContactPhone());
        reservation.setRequirementDesc(dto.getRequirementDesc());
        reservation.setStatus("WAIT_FOLLOW");
        banquetReservationMapper.insert(reservation);
        return BanquetReservationVO.fromEntity(reservation);
    }

    @Override
    public List<BanquetReservationVO> list() {
        return banquetReservationMapper.selectList(new LambdaQueryWrapper<BanquetReservation>()
                .orderByDesc(BanquetReservation::getId))
            .stream()
            .map(BanquetReservationVO::fromEntity)
            .toList();
    }

    @Override
    public BanquetReservationVO detail(Long id) {
        BanquetReservation reservation = banquetReservationMapper.selectById(id);
        if (reservation == null) {
            throw new com.hotel.catering.common.exception.BusinessException("宴席预约不存在");
        }
        return BanquetReservationVO.fromEntity(reservation);
    }
}
