package com.hotel.catering.modules.private_room.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import lombok.Data;

@Data
@TableName("private_room")
public class PrivateRoom {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String coverImage;
    private Integer capacityMin;
    private Integer capacityMax;
    private BigDecimal depositAmount;
    private Integer status;
    private String description;
    private Integer sort;
}
