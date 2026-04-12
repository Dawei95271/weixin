package com.hotel.catering.modules.banquet.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class BanquetReservationCreateDTO {

    @NotBlank(message = "宴席类型不能为空")
    private String banquetType;

    @NotNull(message = "预约日期不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate reserveDate;

    @Min(value = 1, message = "预计人数至少为1")
    private Integer guestCount;

    @NotNull(message = "预算不能为空")
    private BigDecimal budgetAmount;

    @NotBlank(message = "联系人不能为空")
    private String contactName;

    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    private String requirementDesc;
}
