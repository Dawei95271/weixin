package com.hotel.catering.modules.private_room.service;

import com.hotel.catering.modules.private_room.dto.PrivateRoomReserveDTO;
import com.hotel.catering.modules.private_room.vo.PrivateRoomReservationVO;
import com.hotel.catering.modules.private_room.vo.PrivateRoomVO;
import java.time.LocalDate;
import java.util.List;

public interface PrivateRoomService {

    List<PrivateRoomVO> listRooms();

    List<String> listAvailableTimeslots(LocalDate reserveDate);

    PrivateRoomReservationVO reserve(PrivateRoomReserveDTO dto);

    List<PrivateRoomReservationVO> listReservations();
}
