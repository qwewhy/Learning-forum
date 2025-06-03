package com.HongyuanWang.learningforum.handler;

import com.HongyuanWang.learningforum.model.entity.User;
import com.HongyuanWang.learningforum.service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;

@Component
@Slf4j
public class OAuth2LoginSuccessHandler {

    @Resource
    private UserService userService;

    @Value("${google.oauth2.client-id}")
    private String googleClientId;

    /**
     * 验证Google ID Token并处理登录
     */
    public User handleGoogleLogin(String idToken) {
        try {
            // 创建Google ID Token验证器
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            // 验证token
            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken != null) {
                GoogleIdToken.Payload payload = googleIdToken.getPayload();

                // 提取用户信息
                String googleId = payload.getSubject();
                String email = payload.getEmail();
                boolean emailVerified = payload.getEmailVerified();
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");

                // 查找或创建用户
                return userService.findOrCreateGoogleUser(
                        googleId, email, emailVerified, name, pictureUrl
                );
            } else {
                log.error("Invalid Google ID token");
                return null;
            }
        } catch (Exception e) {
            log.error("Error handling Google login", e);
            return null;
        }
    }
}