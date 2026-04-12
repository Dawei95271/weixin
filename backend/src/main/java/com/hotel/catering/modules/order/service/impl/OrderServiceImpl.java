package com.hotel.catering.modules.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hotel.catering.common.exception.BusinessException;
import com.hotel.catering.common.util.NoGenerator;
import com.hotel.catering.modules.order.dto.OrderItemSubmitDTO;
import com.hotel.catering.modules.order.dto.OrderSubmitDTO;
import com.hotel.catering.modules.order.entity.FoodOrder;
import com.hotel.catering.modules.order.entity.FoodOrderItem;
import com.hotel.catering.modules.order.mapper.FoodOrderItemMapper;
import com.hotel.catering.modules.order.mapper.FoodOrderMapper;
import com.hotel.catering.modules.order.service.OrderService;
import com.hotel.catering.modules.order.vo.OrderItemVO;
import com.hotel.catering.modules.order.vo.OrderVO;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final FoodOrderMapper foodOrderMapper;
    private final FoodOrderItemMapper foodOrderItemMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO submit(OrderSubmitDTO dto) {
        if ("ROOM_DELIVERY".equals(dto.getOrderScene()) && (dto.getRoomNo() == null || dto.getRoomNo().isBlank())) {
            throw new BusinessException("客房送餐场景必须填写房号");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        FoodOrder order = new FoodOrder();
        order.setOrderNo(NoGenerator.next("FO"));
        order.setUserId(1L);
        order.setOrderScene(dto.getOrderScene());
        order.setRoomId(dto.getRoomId() == null ? 0L : dto.getRoomId());
        order.setRoomNo(dto.getRoomNo());
        order.setPrivateRoomReservationId(dto.getPrivateRoomReservationId() == null ? 0L : dto.getPrivateRoomReservationId());
        order.setContactName(dto.getContactName());
        order.setContactPhone(dto.getContactPhone());
        order.setRemark(dto.getRemark());

        for (OrderItemSubmitDTO item : dto.getItems()) {
            BigDecimal price = mockPrice(item.getDishId());
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(lineTotal);
        }

        BigDecimal deliveryFee = "ROOM_DELIVERY".equals(dto.getOrderScene()) ? new BigDecimal("5.00") : BigDecimal.ZERO;
        BigDecimal payableAmount = totalAmount.add(deliveryFee);
        order.setItemTotalAmount(totalAmount);
        order.setDeliveryFee(deliveryFee);
        order.setPayableAmount(payableAmount);
        order.setPaidAmount(BigDecimal.ZERO);
        order.setPayStatus("UNPAID");
        order.setOrderStatus("WAIT_PAY");
        foodOrderMapper.insert(order);

        for (OrderItemSubmitDTO item : dto.getItems()) {
            BigDecimal price = mockPrice(item.getDishId());
            FoodOrderItem orderItem = new FoodOrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setDishId(item.getDishId());
            orderItem.setDishSkuId(item.getDishSkuId() == null ? 0L : item.getDishSkuId());
            orderItem.setDishName("菜品-" + item.getDishId());
            orderItem.setSkuName("");
            orderItem.setUnitPrice(price);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setTotalPrice(price.multiply(BigDecimal.valueOf(item.getQuantity())));
            foodOrderItemMapper.insert(orderItem);
        }

        return buildOrderVO(order);
    }

    @Override
    public List<OrderVO> listByUserId(Long userId) {
        return foodOrderMapper.selectList(new LambdaQueryWrapper<FoodOrder>()
                .eq(FoodOrder::getUserId, userId)
                .orderByDesc(FoodOrder::getId))
            .stream()
            .map(this::buildOrderVO)
            .toList();
    }

    @Override
    public OrderVO getDetail(Long id) {
        FoodOrder order = foodOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return buildOrderVO(order);
    }

    private BigDecimal mockPrice(Long dishId) {
        if (dishId == null) {
            return new BigDecimal("0.00");
        }
        return switch ((int) (dishId % 3)) {
            case 1 -> new BigDecimal("38.00");
            case 2 -> new BigDecimal("58.00");
            default -> new BigDecimal("88.00");
        };
    }

    private OrderVO buildOrderVO(FoodOrder order) {
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
}
