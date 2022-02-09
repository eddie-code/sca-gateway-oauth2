package com.edcode.commerce.conf.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

/**
 * @author eddie.lee
 * @date 2022-02-07 22:53
 * @description
 */
@Configuration
@EnableWebSecurity
public class OauthWebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private UserDetailsService userDetailsService;

  @Bean
  public ResourceServerTokenServices tokenServices() {
    RemoteTokenServices tokenServices = new RemoteTokenServices();
    tokenServices.setClientId("orderService");
    tokenServices.setClientSecret("123456");
    tokenServices.setCheckTokenEndpointUrl("http://localhost:9090/oauth/check_token");
    tokenServices.setAccessTokenConverter(getAccessTokenConverter());
    return tokenServices;
  }

  private AccessTokenConverter getAccessTokenConverter() {
    DefaultUserAuthenticationConverter userAuthenticationConverter = new DefaultUserAuthenticationConverter();
    userAuthenticationConverter.setUserDetailsService(userDetailsService);

    DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
    accessTokenConverter.setUserTokenConverter(userAuthenticationConverter);

    return accessTokenConverter;
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    OAuth2AuthenticationManager auth2AuthenticationManager = new OAuth2AuthenticationManager();
    auth2AuthenticationManager.setTokenServices(tokenServices());
    return auth2AuthenticationManager;
  }
}
