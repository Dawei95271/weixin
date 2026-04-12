package com.hotel.catering.modules.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminLoginVO {

    private Long adminUserId;
    private String username;
    private String realName;
    private String token;
}
