package com.hotel.catering.modules.private_room.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hotel.catering.common.exception.BusinessException;
import com.hotel.catering.common.util.NoGenerator;
import com.hotel.catering.modules.private_room.dto.PrivateRoomReserveItemDTO;
import com.hotel.catering.modules.private_room.dto.PrivateRoomReserveDTO;
import com.hotel.catering.modules.private_room.entity.PrivateRoom;
import com.hotel.catering.modules.private_room.entity.PrivateRoomReservation;
import com.hotel.catering.modules.private_room.entity.PrivateRoomReservationItem;
import com.hotel.catering.modules.private_room.mapper.PrivateRoomMapper;
import com.hotel.catering.modules.private_room.mapper.PrivateRoomReservationItemMapper;
import com.hotel.catering.modules.private_room.mapper.PrivateRoomReservationMapper;
import com.hotel.catering.modules.private_room.service.PrivateRoomService;
import com.hotel.catering.modules.private_room.vo.PrivateRoomReservationVO;
import com.hotel.catering.modules.private_room.vo.PrivateRoomVO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PrivateRoomServiceImpl implements PrivateRoomService {

    private static final List<String> DEFAULT_TIMESLOTS = List.of("BREAKFAST", "LUNCH", "DINNER");
    private final PrivateRoomMapper privateRoomMapper;
    private final PrivateRoomReservationMapper privateRoomReservationMapper;
    private final PrivateRoomReservationItemMapper privateRoomReservationItemMapper;

    @Override
    public List<PrivateRoomVO> listRooms() {
        return privateRoomMapper.selectList(new LambdaQueryWrapper<PrivateRoom>()
                .eq(PrivateRoom::getStatus, 1)
                .orderByAsc(PrivateRoom::getSort, PrivateRoom::getId))
            .stream()
            .map(room -> new PrivateRoomVO(
                room.getId(),
                room.getName(),
                room.getCapacityMin(),
                room.getCapacityMax(),
                room.getDepositAmount(),
                room.getDescription()
            ))
            .toList();
    }

    @Override
    public List<String> listAvailableTimeslots(LocalDate reserveDate) {
        return DEFAULT_TIMESLOTS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrivateRoomReservationVO reserve(PrivateRoomReserveDTO dto) {
        PrivateRoom room = privateRoomMapper.selectById(dto.getPrivateRoomId());
        if (room == null || room.getStatus() == null || room.getStatus() != 1) {
            throw new BusinessException("包间不存在");
        }

        Long duplicatedCount = privateRoomReservationMapper.selectCount(new LambdaQueryWrapper<PrivateRoomReservation>()
            .eq(PrivateRoomReservation::getPrivateRoomId, dto.getPrivateRoomId())
            .eq(PrivateRoomReservation::getReserveDate, dto.getReserveDate())
            .eq(PrivateRoomReservation::getTimeslotCode, dto.getTimeslotCode())
            .ne(PrivateRoomReservation::getReservationStatus, "CANCELLED"));
        if (duplicatedCount != null && duplicatedCount > 0) {
            throw new BusinessException("该包间在当前日期和时段已被预约");
        }

        PrivateRoomReservation reservation = new PrivateRoomReservation();
        reservation.setReservationNo(NoGenerator.next("PR"));
        reservation.setUserId(1L);
        reservation.setPrivateRoomId(dto.getPrivateRoomId());
        reservation.setReserveDate(dto.getReserveDate());
        reservation.setTimeslotCode(dto.getTimeslotCode());
        reservation.setGuestCount(dto.getGuestCount());
        reservation.setContactName(dto.getContactName());
        reservation.setContactPhone(dto.getContactPhone());
        reservation.setRemark(dto.getRemark());
        reservation.setDepositAmount(room.getDepositAmount() == null ? BigDecimal.ZERO : room.getDepositAmount());
        reservation.setPayStatus("UNPAID");
        reservation.setReservationStatus("WAIT_PAY");
        privateRoomReservationMapper.insert(reservation);

        List<String> preorderDishes = dto.getItems() == null ? List.of() : dto.getItems().stream().map(item -> {
            PrivateRoomReservationItem reservationItem = buildReservationItem(reservation.getId(), item);
            privateRoomReservationItemMapper.insert(reservationItem);
            return reservationItem.getDishName() + " x" + reservationItem.getQuantity();
        }).toList();

        return new PrivateRoomReservationVO(
            reservation.getId(),
            reservation.getReservationNo(),
            reservation.getPrivateRoomId(),
            reservation.getReserveDate(),
            reservation.getTimeslotCode(),
            reservation.getGuestCount(),
            reservation.getContactName(),
            reservation.getContactPhone(),
            reservation.getDepositAmount(),
            reservation.getReservationStatus(),
            preorderDishes
        );
    }

    @Override
    public List<PrivateRoomReservationVO> listReservations() {
        return privateRoomReservationMapper.selectList(new LambdaQueryWrapper<PrivateRoomReservation>()
                .orderByDesc(PrivateRoomReservation::getReserveDate, PrivateRoomReservation::getId))
            .stream()
            .map(item -> new PrivateRoomReservationVO(
                item.getId(),
                item.getReservationNo(),
                item.getPrivateRoomId(),
                item.getReserveDate(),
                item.getTimeslotCode(),
                item.getGuestCount(),
                item.getContactName(),
                item.getContactPhone(),
                item.getDepositAmount(),
                item.getReservationStatus(),
                listReservationItems(item.getId())
            ))
            .toList();
    }

    private PrivateRoomReservationItem buildReservationItem(Long reservationId, PrivateRoomReserveItemDTO item) {
        BigDecimal unitPrice = mockPrice(item.getDishId());
        PrivateRoomReservationItem reservationItem = new PrivateRoomReservationItem();
        reservationItem.setReservationId(reservationId);
        reservationItem.setDishId(item.getDishId());
        reservationItem.setDishSkuId(0L);
        reservationItem.setDishName("菜品-" + item.getDishId());
        reservationItem.setSkuName("");
        reservationItem.setUnitPrice(unitPrice);
        reservationItem.setQuantity(item.getQuantity());
        reservationItem.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())));
        return reservationItem;
    }

    private List<String> listReservationItems(Long reservationId) {
        return privateRoomReservationItemMapper.selectList(new LambdaQueryWrapper<PrivateRoomReservationItem>()
                .eq(PrivateRoomReservationItem::getReservationId, reservationId))
            .stream()
            .map(item -> item.getDishName() + " x" + item.getQuantity())
            .toList();
    }

    private BigDecimal mockPrice(Long dishId) {
        if (dishId == null) {
            return BigDecimal.ZERO;
        }
        return switch ((int) (dishId % 3)) {
            case 1 -> new BigDecimal("38.00");
            case 2 -> new BigDecimal("58.00");
            default -> new BigDecimal("88.00");
        };
    }
}
