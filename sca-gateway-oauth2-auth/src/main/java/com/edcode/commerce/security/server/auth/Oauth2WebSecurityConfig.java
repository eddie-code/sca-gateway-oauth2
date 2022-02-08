package com.edcode.commerce.security.server.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * @author eddie.lee
 * @date 2022-02-07 21:23
 * @description
 */
@Configuration
@EnableWebSecurity
public class Oauth2WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private LogoutSuccessHandler logoutSuccessHandler;

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * 全局用户信息
   * @param auth 认证管理
   * @throws Exception 用户认证异常信息
   */
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    // 通过传入的用户信息，校验是否这个用户
    auth.userDetailsService(userDetailsService)
        // 通过传入的用户信息，校验密码对不对
        .passwordEncoder(passwordEncoder());
  }

  /**
   * 认证管理: 通过 AuthenticationManagerBuilder 来暴露 spring Bean
   * @return 认证管理对象
   * @throws Exception 认证异常信息
   */
  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  /**
   * http 安全配置
   * @param http
   * @throws Exception
   */
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        .anyRequest().authenticated()
        .and()
        .formLogin()
        .and()
        .httpBasic()
        .and()
        .logout()
        .logoutSuccessHandler(logoutSuccessHandler);
  }

}
