package com.hotel.catering.modules.room.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("room_qrcode")
public class RoomQrcode {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long roomId;
    private String qrCodeKey;
    private Integer status;
}
