package com.hotel.catering.modules.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import lombok.Data;

@Data
@TableName("food_order")
public class FoodOrder {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private String orderScene;
    private Long roomId;
    private String roomNo;
    private Long privateRoomReservationId;
    private String contactName;
    private String contactPhone;
    private String remark;
    private BigDecimal itemTotalAmount;
    private BigDecimal deliveryFee;
    private BigDecimal payableAmount;
    private BigDecimal paidAmount;
    private String payStatus;
    private String orderStatus;
}
