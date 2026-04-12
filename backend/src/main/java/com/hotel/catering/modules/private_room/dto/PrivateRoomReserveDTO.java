package com.hotel.catering.modules.private_room.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class PrivateRoomReserveDTO {

    @NotNull(message = "包间ID不能为空")
    private Long privateRoomId;

    @NotNull(message = "预约日期不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate reserveDate;

    @NotBlank(message = "时段不能为空")
    private String timeslotCode;

    @Min(value = 1, message = "人数至少为1")
    private Integer guestCount;

    @NotBlank(message = "联系人不能为空")
    private String contactName;

    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    private String remark;

    @Valid
    private List<PrivateRoomReserveItemDTO> items;
}
