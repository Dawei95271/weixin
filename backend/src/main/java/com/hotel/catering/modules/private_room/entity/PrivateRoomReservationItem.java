package com.hotel.catering.modules.private_room.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import lombok.Data;

@Data
@TableName("private_room_reservation_item")
public class PrivateRoomReservationItem {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long reservationId;
    private Long dishId;
    private Long dishSkuId;
    private String dishName;
    private String skuName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
}
