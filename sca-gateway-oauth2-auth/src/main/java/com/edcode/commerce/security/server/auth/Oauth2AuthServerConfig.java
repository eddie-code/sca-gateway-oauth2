package com.edcode.commerce.security.server.auth;

import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

/**
 * @author eddie.lee
 * @date 2022-02-07 21:11
 * @description 认证服务器
 */
@Configuration
@EnableAuthorizationServer
public class Oauth2AuthServerConfig extends AuthorizationServerConfigurerAdapter {

  /**
   * 认证管理器
   */
  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private DataSource dataSource;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private UserDetailsService userDetailsService;

  @Bean
  public TokenStore jwtTokenStore() {
    return new JwtTokenStore(jwtAccessTokenConverter());
  }

  @Bean
  protected JwtAccessTokenConverter jwtAccessTokenConverter() {
    JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
    KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("eddie.key"), "123456".toCharArray());
    // 公司私钥
    converter.setKeyPair(keyStoreKeyFactory.getKeyPair("eddie"));
    return converter;
  }

  /**
   * OAuth 授权端点开放
   * 检查服务的访问规则:
   *      权限达表示：isAuthenticated() 身份认证
   *
   * @param security
   * @throws Exception
   */
  @Override
  public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
    security
        // 开启/oauth/token_key验证端口无权限访问  permitAll()
        .tokenKeyAccess("isAuthenticated()")
        // 开启/oauth/check_token验证端口认证权限访问
        .checkTokenAccess("isAuthenticated()")
        //主要是让/oauth/token支持client_id以及client_secret作登录认证
        .allowFormAuthenticationForClients();
  }

  /**
   * OAuth 配置客户端详情信息
   *
   * @param clients
   * @throws Exception
   */
  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    clients
        // 根据jdbc去查询mysql里面的 oauth_client_details 查询数据
        .jdbc(dataSource)
        .passwordEncoder(passwordEncoder);
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
        .tokenStore(jwtTokenStore())
        .tokenEnhancer(jwtAccessTokenConverter())
        .authenticationManager(authenticationManager);
  }

  /**
   * 打印填入，数据库里面值
   * $2a$10$w274e5MEY/42JCQOzjrm8OkC.U83OshSKrWWIOCLZTkB/RTbuJKta
   */
//  public static void main(String[] args) {
//    System.out.println(new BCryptPasswordEncoder().encode("123456"));
//  }

}
