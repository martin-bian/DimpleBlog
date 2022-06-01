package com.dimple.system.controller;

import com.dimple.common.core.constant.CacheConstants;
import com.dimple.common.core.utils.StringUtils;
import com.dimple.common.core.web.controller.BaseController;
import com.dimple.common.core.web.domain.AjaxResult;
import com.dimple.common.core.web.page.TableDataInfo;
import com.dimple.common.log.annotation.Log;
import com.dimple.common.log.enums.BusinessType;
import com.dimple.common.redis.service.RedisService;
import com.dimple.common.security.annotation.RequiresPermissions;
import com.dimple.system.api.model.LoginUser;
import com.dimple.system.domain.SysUserOnline;
import com.dimple.system.service.ISysUserOnlineService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 在线用户监控
 *
 * @author Dimple
 */
@RestController
@RequestMapping("/online")
public class SysUserOnlineController extends BaseController {
    private final ISysUserOnlineService userOnlineService;

    private final RedisService redisService;

    public SysUserOnlineController(ISysUserOnlineService userOnlineService, RedisService redisService) {
        this.userOnlineService = userOnlineService;
        this.redisService = redisService;
    }

    @RequiresPermissions("monitor:online:list")
    @GetMapping("/list")
    public TableDataInfo list(String ipaddr, String userName) {
        Collection<String> keys = redisService.keys(CacheConstants.LOGIN_TOKEN_KEY + "*");
        List<SysUserOnline> userOnlineList = new ArrayList<SysUserOnline>();
        for (String key : keys) {
            LoginUser user = redisService.getCacheObject(key);
            if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName)) {
                if (StringUtils.equals(ipaddr, user.getIpaddr()) && StringUtils.equals(userName, user.getUsername())) {
                    userOnlineList.add(userOnlineService.selectOnlineByInfo(ipaddr, userName, user));
                }
            } else if (StringUtils.isNotEmpty(ipaddr)) {
                if (StringUtils.equals(ipaddr, user.getIpaddr())) {
                    userOnlineList.add(userOnlineService.selectOnlineByIpaddr(ipaddr, user));
                }
            } else if (StringUtils.isNotEmpty(userName)) {
                if (StringUtils.equals(userName, user.getUsername())) {
                    userOnlineList.add(userOnlineService.selectOnlineByUserName(userName, user));
                }
            } else {
                userOnlineList.add(userOnlineService.loginUserToUserOnline(user));
            }
        }
        Collections.reverse(userOnlineList);
        userOnlineList.removeAll(Collections.singleton(null));
        return getDataTable(userOnlineList);
    }

    /**
     * 强退用户
     */
    @RequiresPermissions("monitor:online:forceLogout")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/{tokenId}")
    public AjaxResult forceLogout(@PathVariable String tokenId) {
        redisService.deleteObject(CacheConstants.LOGIN_TOKEN_KEY + tokenId);
        return AjaxResult.success();
    }
}