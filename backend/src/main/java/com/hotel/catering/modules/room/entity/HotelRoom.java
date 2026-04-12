package com.hotel.catering.modules.room.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("hotel_room")
public class HotelRoom {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String buildingNo;
    private Integer floorNo;
    private String roomNo;
    private Integer status;
    private String remark;
}
