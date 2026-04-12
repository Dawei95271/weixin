package com.hotel.catering.modules.private_room.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PrivateRoomReservationVO {

    private Long id;
    private String reservationNo;
    private Long privateRoomId;
    private LocalDate reserveDate;
    private String timeslotCode;
    private Integer guestCount;
    private String contactName;
    private String contactPhone;
    private BigDecimal depositAmount;
    private String reservationStatus;
    private List<String> preorderDishes;
}
