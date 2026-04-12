package com.hotel.catering.modules.banquet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
@TableName("banquet_reservation")
public class BanquetReservation {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String reservationNo;
    private Long userId;
    private String banquetType;
    private LocalDate reserveDate;
    private Integer guestCount;
    private BigDecimal budgetAmount;
    private String contactName;
    private String contactPhone;
    private String requirementDesc;
    private String status;
}
