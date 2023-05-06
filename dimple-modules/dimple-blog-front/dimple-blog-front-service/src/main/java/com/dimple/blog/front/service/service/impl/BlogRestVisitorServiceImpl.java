package com.dimple.blog.front.service.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.dimple.blog.api.bo.GithubVisitorInfoBO;
import com.dimple.blog.api.bo.config.GithubLoginConfig;
import com.dimple.blog.front.service.service.BlogRestConfigService;
import com.dimple.blog.front.service.service.BlogRestVisitorService;
import com.dimple.common.core.utils.StringUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * VisitorServiceImpl
 *
 * @author Dimple
 * @date 2023/3/14 13:38
 */
@Service
@Slf4j
public class BlogRestVisitorServiceImpl implements BlogRestVisitorService {
    @Autowired
    BlogRestConfigService blogRestConfigService;

    @Override
    public GithubVisitorInfoBO getGithubVisitorInfo(String code) {
        log.info("start login with github, code is {}.", code);
        GithubVisitorInfoBO githubVisitorInfoBO = new GithubVisitorInfoBO();
        String token = getAccessToken(code);
        if (StringUtils.isEmpty(token)) {
            return githubVisitorInfoBO;
        }
        JSONObject githubUserInfo = getGithubUserInfo(token);
        githubVisitorInfoBO.setLink(githubUserInfo.getString("html_url"));
        githubVisitorInfoBO.setEmail(githubUserInfo.getString("email"));
        githubVisitorInfoBO.setUsername(githubUserInfo.getString("name"));
        githubVisitorInfoBO.setLoginUsername(githubUserInfo.getString("login"));
        githubVisitorInfoBO.setAvatars(githubUserInfo.getString("avatar_url"));
        githubVisitorInfoBO.setId(githubUserInfo.getLong("id"));
        return githubVisitorInfoBO;
    }

    @SneakyThrows
    private JSONObject getGithubUserInfo(String accessToken) {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .readTimeout(Duration.ofMinutes(1))
                .build();
        Request request = new Request.Builder()
                .url(blogRestConfigService.getGithubLoginConfig().getUserInfoUrl())
                .addHeader("Authorization", "token " + accessToken)
                .get()
                .build();
        Response response = okHttpClient.newCall(request).execute();
        return JSONObject.parseObject(response.body().string());
    }

    @SneakyThrows
    private String getAccessToken(String code) {
        GithubLoginConfig githubLoginConfig = blogRestConfigService.getGithubLoginConfig();
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .readTimeout(Duration.ofMinutes(1))
                .build();
        FormBody formBody = new FormBody.Builder()
                .add("client_id", githubLoginConfig.getClientId())
                .add("client_secret", githubLoginConfig.getClientSecrets())
                .add("code", code)
                .build();
        Request request = new Request.Builder()
                .url(githubLoginConfig.getAccessTokenUrl())
                .post(formBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        //access_token=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&scope=&token_type=bearer
        String responseStr = response.body().string();
        log.info("response is {} ", responseStr);
        return responseStr.substring(0, responseStr.indexOf("&scope")).replace("access_token=", "");
    }

}
