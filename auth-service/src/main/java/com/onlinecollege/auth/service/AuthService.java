package com.onlinecollege.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.onlinecollege.auth.entity.EduUser;
import com.onlinecollege.auth.mapper.EduUserMapper;
import com.onlinecollege.common.exception.BusinessException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final EduUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResult login(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw BusinessException.badRequest("用户名 / 密码不能为空");
        }
        EduUser user = userMapper.selectOne(new LambdaQueryWrapper<EduUser>()
                .eq(EduUser::getUsername, username));
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw BusinessException.unauthorized("用户名或密码错误");
        }
        if (user.getEnabled() != null && user.getEnabled() == 0) {
            throw BusinessException.unauthorized("账号已被禁用");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userMapper.updateById(user);

        String token = jwtService.issue(user.getId(), user.getUsername(), user.getNickName(), user.getRoles());
        log.info("用户登录成功: id={}, username={}", user.getId(), user.getUsername());

        LoginResult result = new LoginResult();
        result.setToken(token);
        result.setUser(toVo(user));
        return result;
    }

    public UserVo me(Long userId) {
        EduUser u = userMapper.selectById(userId);
        if (u == null) {
            throw BusinessException.unauthorized("token 无效：用户不存在");
        }
        return toVo(u);
    }

    private UserVo toVo(EduUser u) {
        UserVo v = new UserVo();
        v.setId(u.getId());
        v.setUsername(u.getUsername());
        v.setNickName(u.getNickName());
        v.setRoles(u.getRoles());
        v.setAvatar(u.getAvatar());
        return v;
    }

    @Data
    public static class LoginResult {
        private String token;
        private UserVo user;
    }

    @Data
    public static class UserVo {
        private Long id;
        private String username;
        private String nickName;
        private String roles;
        private String avatar;
    }
}
