package com.hotel.catering.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hotel.catering.modules.system.entity.BusinessConfig;
import com.hotel.catering.modules.system.mapper.BusinessConfigMapper;
import com.hotel.catering.modules.system.service.BusinessConfigService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessConfigServiceImpl implements BusinessConfigService {

    private static final List<String> PUBLIC_KEYS = List.of(
        "CONTACT_PHONE",
        "DELIVERY_FEE",
        "MIN_ORDER_AMOUNT",
        "BREAKFAST_HOURS",
        "LUNCH_HOURS",
        "DINNER_HOURS",
        "HOME_NOTICE",
        "ROOM_DELIVERY_NOTICE",
        "HOME_BANNERS",
        "HOME_SERVICE_ENTRIES",
        "HOME_TOPIC_CARDS",
        "HOME_FEATURED_DISH_IDS"
    );

    private final BusinessConfigMapper businessConfigMapper;

    @Override
    public Map<String, String> getPublicConfigs() {
        Map<String, String> result = new LinkedHashMap<>();
        businessConfigMapper.selectList(new LambdaQueryWrapper<BusinessConfig>()
                .in(BusinessConfig::getConfigKey, PUBLIC_KEYS))
            .forEach(item -> result.put(item.getConfigKey(), item.getConfigValue() == null ? "" : item.getConfigValue()));
        return result;
    }
}
