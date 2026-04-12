package com.hotel.catering.modules.admin.service;

import com.hotel.catering.modules.admin.dto.AdminBanquetStatusUpdateDTO;
import com.hotel.catering.modules.admin.dto.AdminOrderStatusUpdateDTO;
import com.hotel.catering.modules.admin.dto.AdminReservationStatusUpdateDTO;
import com.hotel.catering.modules.banquet.vo.BanquetReservationVO;
import com.hotel.catering.modules.order.vo.OrderVO;
import com.hotel.catering.modules.private_room.vo.PrivateRoomReservationVO;
import java.util.List;

public interface AdminManagementService {

    List<OrderVO> listOrders();

    OrderVO getOrderDetail(Long orderId);

    OrderVO updateOrderStatus(AdminOrderStatusUpdateDTO dto);

    List<PrivateRoomReservationVO> listPrivateRoomReservations();

    PrivateRoomReservationVO getPrivateRoomReservationDetail(Long reservationId);

    PrivateRoomReservationVO updatePrivateRoomReservationStatus(AdminReservationStatusUpdateDTO dto);

    List<BanquetReservationVO> listBanquetReservations();

    BanquetReservationVO getBanquetReservationDetail(Long reservationId);

    BanquetReservationVO updateBanquetReservationStatus(AdminBanquetStatusUpdateDTO dto);
}
