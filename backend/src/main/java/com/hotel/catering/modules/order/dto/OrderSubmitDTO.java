package com.hotel.catering.modules.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
public class OrderSubmitDTO {

    @NotBlank(message = "订单场景不能为空")
    private String orderScene;

    private Long roomId;

    private String roomNo;

    private Long privateRoomReservationId;

    @NotBlank(message = "联系人不能为空")
    private String contactName;

    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    private String remark;

    @Valid
    @NotEmpty(message = "订单明细不能为空")
    private List<OrderItemSubmitDTO> items;
}
