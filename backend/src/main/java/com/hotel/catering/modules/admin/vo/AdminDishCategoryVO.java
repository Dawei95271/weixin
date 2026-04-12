package com.hotel.catering.modules.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminDishCategoryVO {

    private Long id;
    private String name;
    private Integer sort;
    private Integer status;
}
