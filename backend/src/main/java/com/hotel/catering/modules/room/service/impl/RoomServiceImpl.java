package com.hotel.catering.modules.room.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hotel.catering.common.exception.BusinessException;
import com.hotel.catering.modules.room.entity.HotelRoom;
import com.hotel.catering.modules.room.entity.RoomQrcode;
import com.hotel.catering.modules.room.mapper.HotelRoomMapper;
import com.hotel.catering.modules.room.mapper.RoomQrcodeMapper;
import com.hotel.catering.modules.room.service.RoomService;
import com.hotel.catering.modules.room.vo.RoomScanVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomQrcodeMapper roomQrcodeMapper;
    private final HotelRoomMapper hotelRoomMapper;

    @Override
    public RoomScanVO scan(String code) {
        RoomQrcode roomQrcode = roomQrcodeMapper.selectOne(new LambdaQueryWrapper<RoomQrcode>()
            .eq(RoomQrcode::getQrCodeKey, code)
            .eq(RoomQrcode::getStatus, 1)
            .last("limit 1"));
        if (roomQrcode == null) {
            throw new BusinessException("二维码无效或已失效");
        }
        HotelRoom room = hotelRoomMapper.selectById(roomQrcode.getRoomId());
        if (room == null || room.getStatus() == null || room.getStatus() != 1) {
            throw new BusinessException("房间不存在或未启用");
        }
        return new RoomScanVO(room.getId(), room.getRoomNo(), room.getFloorNo(), "ROOM_DELIVERY");
    }
}
