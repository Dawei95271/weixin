package com.hotel.catering.modules.banquet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("banquet_follow_record")
public class BanquetFollowRecord {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long banquetReservationId;
    private String followContent;
    private Long followUserId;
    private LocalDateTime nextFollowTime;
}
