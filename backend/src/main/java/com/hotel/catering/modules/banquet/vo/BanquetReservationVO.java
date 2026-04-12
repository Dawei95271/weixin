package com.hotel.catering.modules.banquet.vo;

import com.hotel.catering.modules.banquet.entity.BanquetReservation;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BanquetReservationVO {

    private Long id;
    private String reservationNo;
    private String banquetType;
    private LocalDate reserveDate;
    private Integer guestCount;
    private BigDecimal budgetAmount;
    private String contactName;
    private String contactPhone;
    private String requirementDesc;
    private String status;
    private LocalDateTime createdAt;

    public static BanquetReservationVO fromEntity(BanquetReservation entity) {
        return new BanquetReservationVO(
            entity.getId(),
            entity.getReservationNo(),
            entity.getBanquetType(),
            entity.getReserveDate(),
            entity.getGuestCount(),
            entity.getBudgetAmount(),
            entity.getContactName(),
            entity.getContactPhone(),
            entity.getRequirementDesc(),
            entity.getStatus(),
            null
        );
    }
}
