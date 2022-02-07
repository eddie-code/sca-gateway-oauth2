package com.edcode.commerce.security.server.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author eddie.lee
 * @date 2022-02-07 21:30
 * @description
 */
@Component("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

  /**
   * 使用随机盐加密
   */
  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return User.withUsername(username)
        .password(passwordEncoder.encode("123456"))
        .authorities("ROLE_ADMIN")
        .build();
  }

}
