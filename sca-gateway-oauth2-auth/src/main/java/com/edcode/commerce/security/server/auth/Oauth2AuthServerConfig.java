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
 * @description ???????????????
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
   * OAuth ??????????????????
   * ???????????????????????????:
   *      ??????????????????isAuthenticated() ????????????
   *
   * @param security
   * @throws Exception
   */
  @Override
  public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
    security
        // ??????/oauth/token_key???????????????????????????  permitAll() isAuthenticated()
        .tokenKeyAccess("permitAll()")
        // ??????/oauth/check_token??????????????????????????????
        .checkTokenAccess("permitAll()")
        //????????????/oauth/token??????client_id??????client_secret???????????????
        .allowFormAuthenticationForClients();
  }

  /**
   * OAuth ???????????????????????????
   *
   * @param clients
   * @throws Exception
   */
  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    clients.withClientDetails(clientDetailsService());
  }

  private ClientDetailsService clientDetailsService() {
    // ??????jdbc?????????mysql????????? oauth_client_details ????????????
    JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
    clientDetailsService.setPasswordEncoder(passwordEncoder);
    return clientDetailsService;
  }

  @Bean
  public AuthorizationCodeServices authorizationCodeServices() {
    // ????????????????????????????????????????????????????????????
    return new JdbcAuthorizationCodeServices(dataSource);
  }

  @Bean
  public AuthorizationServerTokenServices tokenService() {
    DefaultTokenServices services = new DefaultTokenServices();
    // ?????????????????????
    services.setClientDetailsService(clientDetailsService());
    // ????????????????????????
    services.setSupportRefreshToken(true);
    // ??????????????????
    services.setTokenStore(jwtTokenStore());
    // JWT ????????????
    TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
    List<TokenEnhancer> enhancerList = new ArrayList<>();
    // ??????JWT??????????????????
    enhancerList.add(jwtTokenEnhancer);
    enhancerList.add(jwtAccessTokenConverter());
    enhancerChain.setTokenEnhancers(enhancerList);
    services.setTokenEnhancer(enhancerChain);
    // ?????????????????? ???????????????????????????
//    services.setAccessTokenValiditySeconds(7200);
    // ???????????????????????? ???????????????????????????
//    services.setRefreshTokenValiditySeconds(29000);

    return services;
  }

  /**
   * ??????????????????
   *
   * @param endpoints
   * @throws Exception
   */
  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    //???????????????
    endpoints.authenticationManager(authenticationManager)
        .accessTokenConverter(jwtAccessTokenConverter())
        // ???????????????
        .authorizationCodeServices(authorizationCodeServices())
        // ??????????????????
        .tokenServices(tokenService())
        // ?????? post ??????
        .allowedTokenEndpointRequestMethods(HttpMethod.POST);
  }

  /**
   * ?????????????????????????????????
   * $2a$10$w274e5MEY/42JCQOzjrm8OkC.U83OshSKrWWIOCLZTkB/RTbuJKta
   */
//  public static void main(String[] args) {
//    System.out.println(new BCryptPasswordEncoder().encode("123456"));
//  }

}
