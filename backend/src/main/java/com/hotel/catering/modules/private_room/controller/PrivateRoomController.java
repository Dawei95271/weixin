package com.hotel.catering.modules.private_room.controller;

import com.hotel.catering.common.api.ApiResponse;
import com.hotel.catering.modules.private_room.dto.PrivateRoomReserveDTO;
import com.hotel.catering.modules.private_room.service.PrivateRoomService;
import com.hotel.catering.modules.private_room.vo.PrivateRoomReservationVO;
import com.hotel.catering.modules.private_room.vo.PrivateRoomVO;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/private-room")
@RequiredArgsConstructor
public class PrivateRoomController {

    private final PrivateRoomService privateRoomService;

    @GetMapping("/list")
    public ApiResponse<List<PrivateRoomVO>> listRooms() {
        return ApiResponse.success(privateRoomService.listRooms());
    }

    @GetMapping("/available-timeslots")
    public ApiResponse<List<String>> listAvailableTimeslots(
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate reserveDate
    ) {
        return ApiResponse.success(privateRoomService.listAvailableTimeslots(reserveDate));
    }

    @PostMapping("/reserve")
    public ApiResponse<PrivateRoomReservationVO> reserve(@Valid @RequestBody PrivateRoomReserveDTO dto) {
        return ApiResponse.success(privateRoomService.reserve(dto));
    }

    @GetMapping("/reservation/list")
    public ApiResponse<List<PrivateRoomReservationVO>> listReservations() {
        return ApiResponse.success(privateRoomService.listReservations());
    }

    @GetMapping("/reservation/detail/{id}")
    public ApiResponse<PrivateRoomReservationVO> reservationDetail(@PathVariable Long id) {
        return ApiResponse.success(privateRoomService.getReservationDetail(id));
    }
}
