package com.hotel.catering.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.hotel.catering.modules", markerInterface = BaseMapper.class)
public class MybatisPlusConfig {
}
