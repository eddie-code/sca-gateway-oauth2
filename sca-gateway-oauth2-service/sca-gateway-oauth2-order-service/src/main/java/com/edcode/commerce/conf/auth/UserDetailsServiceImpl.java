package com.edcode.commerce.conf.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @author eddie.lee
 * @date 2022-02-07 23:15
 * @description
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

  @Override
  public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    return User.builder()
        .username(s)
        .id(1L)
        .build();
  }
}
