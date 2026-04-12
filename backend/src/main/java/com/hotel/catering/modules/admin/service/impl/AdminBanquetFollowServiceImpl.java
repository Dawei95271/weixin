package com.hotel.catering.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hotel.catering.common.exception.BusinessException;
import com.hotel.catering.modules.admin.dto.AdminBanquetFollowCreateDTO;
import com.hotel.catering.modules.admin.service.AdminBanquetFollowService;
import com.hotel.catering.modules.admin.vo.AdminBanquetFollowVO;
import com.hotel.catering.modules.banquet.entity.BanquetFollowRecord;
import com.hotel.catering.modules.banquet.entity.BanquetReservation;
import com.hotel.catering.modules.banquet.mapper.BanquetFollowRecordMapper;
import com.hotel.catering.modules.banquet.mapper.BanquetReservationMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminBanquetFollowServiceImpl implements AdminBanquetFollowService {

    private final BanquetReservationMapper banquetReservationMapper;
    private final BanquetFollowRecordMapper banquetFollowRecordMapper;

    @Override
    public List<AdminBanquetFollowVO> list(Long reservationId) {
        return banquetFollowRecordMapper.selectList(new LambdaQueryWrapper<BanquetFollowRecord>()
                .eq(BanquetFollowRecord::getBanquetReservationId, reservationId)
                .orderByDesc(BanquetFollowRecord::getId))
            .stream()
            .map(item -> new AdminBanquetFollowVO(
                item.getId(),
                item.getFollowContent(),
                item.getNextFollowTime(),
                null
            ))
            .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminBanquetFollowVO create(AdminBanquetFollowCreateDTO dto) {
        BanquetReservation reservation = banquetReservationMapper.selectById(dto.getReservationId());
        if (reservation == null) {
            throw new BusinessException("宴席预约不存在");
        }
        BanquetFollowRecord record = new BanquetFollowRecord();
        record.setBanquetReservationId(dto.getReservationId());
        record.setFollowContent(dto.getFollowContent());
        record.setFollowUserId(1L);
        record.setNextFollowTime(parseNextFollowTime(dto.getNextFollowTime()));
        banquetFollowRecordMapper.insert(record);
        return new AdminBanquetFollowVO(
            record.getId(),
            record.getFollowContent(),
            record.getNextFollowTime(),
            null
        );
    }

    private LocalDateTime parseNextFollowTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException ex) {
            throw new BusinessException("下次跟进时间格式错误，应为 ISO 时间格式");
        }
    }
}
