package com.hotel.catering.modules.admin.vo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminBanquetFollowVO {

    private Long id;
    private String followContent;
    private LocalDateTime nextFollowTime;
    private LocalDateTime createdAt;
}
