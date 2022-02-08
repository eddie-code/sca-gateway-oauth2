package com.edcode.commerce.security.server.auth;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author eddie.lee
 * @date 2022-02-08 10:08
 * @description 声明称Spring的Component 然后 接收redirect_uri参数并跳转
 */
@Component
public class Oauth2LogoutSuccessHandler implements LogoutSuccessHandler {

  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    String redirectUri = request.getParameter("redirect_uri");
    if(!StringUtils.isEmpty(redirectUri)) {
      response.sendRedirect(redirectUri);
    }
  }
}
