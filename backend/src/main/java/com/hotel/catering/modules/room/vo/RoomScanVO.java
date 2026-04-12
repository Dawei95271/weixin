package com.hotel.catering.modules.room.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomScanVO {

    private Long roomId;
    private String roomNo;
    private Integer floorNo;
    private String scene;
}
