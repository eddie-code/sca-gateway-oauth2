package com.edcode.commerce.conf.auth;

import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author eddie.lee
 * @date 2022-02-07 23:16
 * @description
 */
@Builder
public class User implements UserDetails {

  @Getter
  @Setter
  private Long id;

  @Getter
  @Setter
  private String username;

  @Getter
  @Setter
  private String password;

  /**  权限列表  */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ADMIN");
  }

  /**  用户是否过期  */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**  用户是否锁定  */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /**  用户是否过期  */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /**  用户是否启用  */
  @Override
  public boolean isEnabled() {
    return true;
  }
}
