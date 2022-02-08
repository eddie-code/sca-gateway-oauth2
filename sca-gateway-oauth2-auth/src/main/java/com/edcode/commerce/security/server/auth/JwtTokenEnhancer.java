package com.edcode.commerce.security.server.auth;

import java.util.HashMap;
import java.util.Map;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

/**
 * @author eddie.lee
 * @date 2022-02-08 10:34
 * @description jwt 扩展
 */
public class JwtTokenEnhancer implements TokenEnhancer {

  @Override
  public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
    Map<String, Object> info = new HashMap<>(16);
    info.put("provider", "jwt");
    info.put("state", "cn");
    //设置附加信息
    ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(info);
    return oAuth2AccessToken;
  }
}

