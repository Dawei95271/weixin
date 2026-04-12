package com.hotel.catering.modules.private_room.vo;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PrivateRoomVO {

    private Long id;
    private String name;
    private Integer capacityMin;
    private Integer capacityMax;
    private BigDecimal depositAmount;
    private String description;
}
