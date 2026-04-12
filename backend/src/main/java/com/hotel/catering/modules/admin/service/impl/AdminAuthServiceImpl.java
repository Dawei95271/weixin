package com.hotel.catering.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hotel.catering.common.exception.BusinessException;
import com.hotel.catering.modules.admin.dto.AdminLoginDTO;
import com.hotel.catering.modules.admin.entity.AdminUser;
import com.hotel.catering.modules.admin.mapper.AdminUserMapper;
import com.hotel.catering.modules.admin.service.AdminAuthService;
import com.hotel.catering.modules.admin.vo.AdminLoginVO;
import com.hotel.catering.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AdminUserMapper adminUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AdminLoginVO login(AdminLoginDTO dto) {
        AdminUser adminUser = adminUserMapper.selectOne(new LambdaQueryWrapper<AdminUser>()
            .eq(AdminUser::getUsername, dto.getUsername())
            .last("limit 1"));
        if (adminUser == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (adminUser.getStatus() == null || adminUser.getStatus() != 1) {
            throw new BusinessException("账号已停用");
        }
        if (!passwordEncoder.matches(dto.getPassword(), adminUser.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        String token = jwtTokenProvider.createToken(adminUser.getId(), adminUser.getUsername());
        return new AdminLoginVO(adminUser.getId(), adminUser.getUsername(), adminUser.getRealName(), token);
    }
}
