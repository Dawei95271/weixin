package com.hotel.catering.modules.order.service;

import com.hotel.catering.modules.order.dto.OrderSubmitDTO;
import com.hotel.catering.modules.order.vo.OrderVO;
import java.util.List;

public interface OrderService {

    OrderVO submit(OrderSubmitDTO dto);

    List<OrderVO> listByUserId(Long userId);

    OrderVO getDetail(Long id);
}
