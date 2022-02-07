package com.edcode.commerce.conf.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 * @author eddie.lee
 * @date 2022-02-07 22:47
 * @description 资源服务器
 */
@Configuration
@EnableResourceServer
public class Oauth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

  @Override
  public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
    resources.resourceId("order-server");
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    // 除了 hello 请求不需要身份认证，其他请求都需要
    http.authorizeRequests().antMatchers("/hello").permitAll().anyRequest().authenticated();
  }
}
