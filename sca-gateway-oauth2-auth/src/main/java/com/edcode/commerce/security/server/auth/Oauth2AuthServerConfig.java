package com.edcode.commerce.security.server.auth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
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

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private DataSource dataSource;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private JwtTokenEnhancer jwtTokenEnhancer;

  @Bean
  public TokenStore jwtTokenStore() {
    return new JwtTokenStore(jwtAccessTokenConverter());
  }

  @Bean
  public JwtTokenEnhancer jwtTokenEnhancer() {
    return new JwtTokenEnhancer();
  }

  /**
   * keytool -genkey -alias eddie-jwt -keyalg RSA -keysize 1024 -keystore eddie-jwt.jks -validity 3650 -keypass eddie00 -storepass eddie00
   * keytool -list -rfc --keystore eddie-jwt.jks | openssl x509 -inform pem -pubkey
   */
  @Bean
  protected JwtAccessTokenConverter jwtAccessTokenConverter() {
    JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
    KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("eddie-jwt.jks"), "eddie00".toCharArray());
    converter.setKeyPair(keyStoreKeyFactory.getKeyPair("eddie-jwt"));
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
        // 开启/oauth/token_key验证端口无权限访问  permitAll() isAuthenticated()
        .tokenKeyAccess("permitAll()")
        // 开启/oauth/check_token验证端口认证权限访问
        .checkTokenAccess("permitAll()")
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
    clients.withClientDetails(clientDetailsService());
  }

  private ClientDetailsService clientDetailsService() {
    // 根据jdbc去查询mysql里面的 oauth_client_details 查询数据
    JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
    clientDetailsService.setPasswordEncoder(passwordEncoder);
    return clientDetailsService;
  }

  @Bean
  public AuthorizationCodeServices authorizationCodeServices() {
    // 设置授权码模式的授权码如何存储（数据库）
    return new JdbcAuthorizationCodeServices(dataSource);
  }

  @Bean
  public AuthorizationServerTokenServices tokenService() {
    DefaultTokenServices services = new DefaultTokenServices();
    // 客户端信息服务
    services.setClientDetailsService(clientDetailsService());
    // 是否产生刷新令牌
    services.setSupportRefreshToken(true);
    // 令牌存储策略
    services.setTokenStore(jwtTokenStore());
    // JWT 令牌增强
    TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
    List<TokenEnhancer> enhancerList = new ArrayList<>();
    // 配置JWT的内容增强器
    enhancerList.add(jwtTokenEnhancer);
    enhancerList.add(jwtAccessTokenConverter());
    enhancerChain.setTokenEnhancers(enhancerList);
    services.setTokenEnhancer(enhancerChain);
    // 生成有效时间 （通过数据库指定）
//    services.setAccessTokenValiditySeconds(7200);
    // 刷新令牌有效时间 （通过数据库指定）
//    services.setRefreshTokenValiditySeconds(29000);

    return services;
  }

  /**
   * 令牌访问端点
   *
   * @param endpoints
   * @throws Exception
   */
  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    //认证管理器
    endpoints.authenticationManager(authenticationManager)
        .accessTokenConverter(jwtAccessTokenConverter())
        // 授权码服务
        .authorizationCodeServices(authorizationCodeServices())
        // 令牌管理服务
        .tokenServices(tokenService())
        // 允许 post 提交
        .allowedTokenEndpointRequestMethods(HttpMethod.POST);
  }

  /**
   * 打印填入，数据库里面值
   * $2a$10$w274e5MEY/42JCQOzjrm8OkC.U83OshSKrWWIOCLZTkB/RTbuJKta
   */
//  public static void main(String[] args) {
//    System.out.println(new BCryptPasswordEncoder().encode("123456"));
//  }

}
