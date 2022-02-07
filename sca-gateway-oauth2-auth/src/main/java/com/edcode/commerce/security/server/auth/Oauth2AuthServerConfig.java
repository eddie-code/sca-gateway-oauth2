package com.edcode.commerce.security.server.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

/**
 * @author eddie.lee
 * @date 2022-02-07 21:11
 * @description 认证服务器
 */
@Configuration
@EnableAuthorizationServer
public class Oauth2AuthServerConfig extends AuthorizationServerConfigurerAdapter {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  /**
   * 检查服务的访问规则:
   *      权限达表示：isAuthenticated() 身份认证
   *
   * @param security
   * @throws Exception
   */
  @Override
  public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
    security
        .checkTokenAccess("isAuthenticated()");
  }

  /**
   * 客户端的详情信息
   *
   * @param clients
   * @throws Exception
   */
  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    clients.inMemory()
        // 客户端id
        .withClient("orderApp")
        // 客户端密码
        .secret(passwordEncoder.encode("123456"))
        // 该客户端允许授权的范围
        .scopes("read", "write")
        // 该客户端允许授权的时间为一小时
        .accessTokenValiditySeconds(3600)
        // 资源服务器的id: 逗号分隔
        .resourceIds("order-server")
        // 该客户端允许授权的类型 "authorization_code", "password", "client_credentials", "implicit", "refresh_token"
				.authorizedGrantTypes("password")

        .and()
        // 客户端id
        .withClient("orderService")
        // 客户端密码
        .secret(passwordEncoder.encode("123456"))
        // 该客户端允许授权的范围
        .scopes("read")
        // 该客户端允许授权的时间为一小时
        .accessTokenValiditySeconds(3600)
        // 资源服务器的id: 逗号分隔
        .resourceIds("order-server")
        // 该客户端允许授权的类型 "authorization_code", "password", "client_credentials", "implicit", "refresh_token"
        .authorizedGrantTypes("password");
  }

  /**
   * 校验传入的用户信息是否合法的
   *
   * @param endpoints
   * @throws Exception
   */
  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    endpoints
        .userDetailsService(userDetailsService)
        .authenticationManager(authenticationManager);
  }

}
