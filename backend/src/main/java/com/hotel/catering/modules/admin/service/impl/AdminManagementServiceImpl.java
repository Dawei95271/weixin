package com.hotel.catering.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hotel.catering.common.exception.BusinessException;
import com.hotel.catering.modules.admin.dto.AdminBanquetStatusUpdateDTO;
import com.hotel.catering.modules.admin.dto.AdminOrderStatusUpdateDTO;
import com.hotel.catering.modules.admin.dto.AdminReservationStatusUpdateDTO;
import com.hotel.catering.modules.admin.service.AdminManagementService;
import com.hotel.catering.modules.banquet.entity.BanquetReservation;
import com.hotel.catering.modules.banquet.mapper.BanquetReservationMapper;
import com.hotel.catering.modules.banquet.vo.BanquetReservationVO;
import com.hotel.catering.modules.order.entity.FoodOrder;
import com.hotel.catering.modules.order.entity.FoodOrderItem;
import com.hotel.catering.modules.order.mapper.FoodOrderItemMapper;
import com.hotel.catering.modules.order.mapper.FoodOrderMapper;
import com.hotel.catering.modules.order.vo.OrderItemVO;
import com.hotel.catering.modules.order.vo.OrderVO;
import com.hotel.catering.modules.private_room.entity.PrivateRoomReservation;
import com.hotel.catering.modules.private_room.entity.PrivateRoomReservationItem;
import com.hotel.catering.modules.private_room.mapper.PrivateRoomReservationItemMapper;
import com.hotel.catering.modules.private_room.mapper.PrivateRoomReservationMapper;
import com.hotel.catering.modules.private_room.vo.PrivateRoomReservationVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminManagementServiceImpl implements AdminManagementService {

    private final FoodOrderMapper foodOrderMapper;
    private final FoodOrderItemMapper foodOrderItemMapper;
    private final PrivateRoomReservationMapper privateRoomReservationMapper;
    private final PrivateRoomReservationItemMapper privateRoomReservationItemMapper;
    private final BanquetReservationMapper banquetReservationMapper;

    @Override
    public List<OrderVO> listOrders() {
        return foodOrderMapper.selectList(new LambdaQueryWrapper<FoodOrder>()
                .orderByDesc(FoodOrder::getId))
            .stream()
            .map(this::toOrderVO)
            .toList();
    }

    @Override
    public OrderVO getOrderDetail(Long orderId) {
        FoodOrder order = foodOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return toOrderVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO updateOrderStatus(AdminOrderStatusUpdateDTO dto) {
        FoodOrder order = foodOrderMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        order.setOrderStatus(dto.getOrderStatus());
        foodOrderMapper.updateById(order);
        return toOrderVO(order);
    }

    @Override
    public List<PrivateRoomReservationVO> listPrivateRoomReservations() {
        return privateRoomReservationMapper.selectList(new LambdaQueryWrapper<PrivateRoomReservation>()
                .orderByDesc(PrivateRoomReservation::getReserveDate, PrivateRoomReservation::getId))
            .stream()
            .map(this::toPrivateRoomReservationVO)
            .toList();
    }

    @Override
    public PrivateRoomReservationVO getPrivateRoomReservationDetail(Long reservationId) {
        PrivateRoomReservation reservation = privateRoomReservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new BusinessException("包间预约不存在");
        }
        return toPrivateRoomReservationVO(reservation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrivateRoomReservationVO updatePrivateRoomReservationStatus(AdminReservationStatusUpdateDTO dto) {
        PrivateRoomReservation reservation = privateRoomReservationMapper.selectById(dto.getReservationId());
        if (reservation == null) {
            throw new BusinessException("包间预约不存在");
        }
        reservation.setReservationStatus(dto.getReservationStatus());
        privateRoomReservationMapper.updateById(reservation);
        return toPrivateRoomReservationVO(reservation);
    }

    @Override
    public List<BanquetReservationVO> listBanquetReservations() {
        return banquetReservationMapper.selectList(new LambdaQueryWrapper<BanquetReservation>()
                .orderByDesc(BanquetReservation::getId))
            .stream()
            .map(BanquetReservationVO::fromEntity)
            .toList();
    }

    @Override
    public BanquetReservationVO getBanquetReservationDetail(Long reservationId) {
        BanquetReservation reservation = banquetReservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new BusinessException("宴席预约不存在");
        }
        return BanquetReservationVO.fromEntity(reservation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BanquetReservationVO updateBanquetReservationStatus(AdminBanquetStatusUpdateDTO dto) {
        BanquetReservation reservation = banquetReservationMapper.selectById(dto.getReservationId());
        if (reservation == null) {
            throw new BusinessException("宴席预约不存在");
        }
        reservation.setStatus(dto.getStatus());
        banquetReservationMapper.updateById(reservation);
        return BanquetReservationVO.fromEntity(reservation);
    }

    private OrderVO toOrderVO(FoodOrder order) {
        List<OrderItemVO> items = foodOrderItemMapper.selectList(new LambdaQueryWrapper<FoodOrderItem>()
                .eq(FoodOrderItem::getOrderId, order.getId()))
            .stream()
            .map(item -> new OrderItemVO(
                item.getDishId(),
                item.getDishName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotalPrice()
            ))
            .toList();
        return new OrderVO(
            order.getId(),
            order.getOrderNo(),
            order.getOrderScene(),
            order.getContactName(),
            order.getContactPhone(),
            order.getRoomNo(),
            order.getOrderStatus(),
            order.getPayableAmount(),
            null,
            items
        );
    }

    private PrivateRoomReservationVO toPrivateRoomReservationVO(PrivateRoomReservation reservation) {
        List<String> preorderDishes = privateRoomReservationItemMapper.selectList(
                new LambdaQueryWrapper<PrivateRoomReservationItem>()
                    .eq(PrivateRoomReservationItem::getReservationId, reservation.getId()))
            .stream()
            .map(item -> item.getDishName() + " x" + item.getQuantity())
            .toList();
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
}
