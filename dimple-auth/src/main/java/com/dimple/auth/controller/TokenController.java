package com.dimple.auth.controller;

import com.dimple.auth.form.LoginBody;
import com.dimple.auth.form.RegisterBody;
import com.dimple.auth.service.SysLoginService;
import com.dimple.common.core.domain.R;
import com.dimple.common.core.utils.JwtUtils;
import com.dimple.common.core.utils.StringUtils;
import com.dimple.common.security.auth.AuthUtil;
import com.dimple.common.security.service.TokenService;
import com.dimple.common.security.utils.SecurityUtils;
import com.dimple.system.api.model.LoginUser;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * token 控制
 *
 * @author Dimple
 */
@RestController
public class TokenController {
    private final TokenService tokenService;

    private final SysLoginService sysLoginService;

    public TokenController(TokenService tokenService, SysLoginService sysLoginService) {
        this.tokenService = tokenService;
        this.sysLoginService = sysLoginService;
    }

    @PostMapping("login")
    public R<?> login(@RequestBody LoginBody form) {
        // 用户登录
        LoginUser userInfo = sysLoginService.login(form.getUsername(), form.getPassword());
        // 获取登录token
        return R.ok(tokenService.createToken(userInfo));
    }

    @DeleteMapping("logout")
    public R<?> logout(HttpServletRequest request) {
        String token = SecurityUtils.getToken(request);
        if (StringUtils.isNotEmpty(token)) {
            String username = JwtUtils.getUserName(token);
            // 删除用户缓存记录
            AuthUtil.logoutByToken(token);
            // 记录用户退出日志
            sysLoginService.logout(username);
        }
        return R.ok();
    }

    @PostMapping("refresh")
    public R<?> refresh(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser)) {
            // 刷新令牌有效期
            tokenService.refreshToken(loginUser);
            return R.ok();
        }
        return R.ok();
    }

    @PostMapping("register")
    public R<?> register(@RequestBody RegisterBody registerBody) {
        // 用户注册
        sysLoginService.register(registerBody.getUsername(), registerBody.getPassword());
        return R.ok();
    }
}