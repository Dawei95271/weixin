package com.hotel.catering.modules.banquet.controller;

import com.hotel.catering.common.api.ApiResponse;
import com.hotel.catering.modules.banquet.dto.BanquetReservationCreateDTO;
import com.hotel.catering.modules.banquet.service.BanquetService;
import com.hotel.catering.modules.banquet.vo.BanquetReservationVO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/banquet/reservation")
@RequiredArgsConstructor
public class BanquetController {

    private final BanquetService banquetService;

    @PostMapping("/create")
    public ApiResponse<BanquetReservationVO> create(@Valid @RequestBody BanquetReservationCreateDTO dto) {
        return ApiResponse.success(banquetService.create(dto));
    }

    @GetMapping("/list")
    public ApiResponse<List<BanquetReservationVO>> list() {
        return ApiResponse.success(banquetService.list());
    }

    @GetMapping("/detail/{id}")
    public ApiResponse<BanquetReservationVO> detail(@PathVariable Long id) {
        return ApiResponse.success(banquetService.detail(id));
    }
}
