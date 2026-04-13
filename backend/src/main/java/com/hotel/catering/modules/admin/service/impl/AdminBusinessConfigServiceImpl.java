package com.hotel.catering.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hotel.catering.modules.admin.dto.AdminBusinessConfigSaveDTO;
import com.hotel.catering.modules.admin.dto.AdminBusinessConfigSaveItemDTO;
import com.hotel.catering.modules.admin.service.AdminBusinessConfigService;
import com.hotel.catering.modules.admin.vo.AdminBusinessConfigVO;
import com.hotel.catering.modules.system.entity.BusinessConfig;
import com.hotel.catering.modules.system.mapper.BusinessConfigMapper;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminBusinessConfigServiceImpl implements AdminBusinessConfigService {

    private final BusinessConfigMapper businessConfigMapper;

    @Override
    public List<AdminBusinessConfigVO> list() {
        return businessConfigMapper.selectList(new LambdaQueryWrapper<BusinessConfig>()
                .orderByAsc(BusinessConfig::getId))
            .stream()
            .map(this::toVO)
            .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<AdminBusinessConfigVO> save(AdminBusinessConfigSaveDTO dto) {
        Map<String, BusinessConfig> existingMap = businessConfigMapper.selectList(new LambdaQueryWrapper<BusinessConfig>())
            .stream()
            .collect(Collectors.toMap(BusinessConfig::getConfigKey, Function.identity()));
        for (AdminBusinessConfigSaveItemDTO item : dto.getItems()) {
            BusinessConfig config = existingMap.getOrDefault(item.getConfigKey(), new BusinessConfig());
            config.setConfigKey(item.getConfigKey());
            config.setConfigName(item.getConfigName());
            config.setConfigValue(item.getConfigValue() == null ? "" : item.getConfigValue());
            if (config.getId() == null) {
                businessConfigMapper.insert(config);
            } else {
                businessConfigMapper.updateById(config);
            }
        }
        return list();
    }

    private AdminBusinessConfigVO toVO(BusinessConfig config) {
        return new AdminBusinessConfigVO(
            config.getId(),
            config.getConfigKey(),
            config.getConfigName(),
            config.getConfigValue()
        );
    }
}
