package com.hotel.catering.modules.room.controller;

import com.hotel.catering.common.api.ApiResponse;
import com.hotel.catering.modules.room.service.RoomService;
import com.hotel.catering.modules.room.vo.RoomScanVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/scan/{code}")
    public ApiResponse<RoomScanVO> scan(@PathVariable String code) {
        return ApiResponse.success(roomService.scan(code));
    }
}
