package com.hotel.catering.modules.private_room.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
@TableName("private_room_reservation")
public class PrivateRoomReservation {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String reservationNo;
    private Long userId;
    private Long privateRoomId;
    private LocalDate reserveDate;
    private String timeslotCode;
    private Integer guestCount;
    private String contactName;
    private String contactPhone;
    private String remark;
    private BigDecimal depositAmount;
    private String payStatus;
    private String reservationStatus;
}
