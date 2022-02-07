package com.edcode.commerce.controller;

import com.edcode.commerce.conf.auth.User;
import com.edcode.commerce.entity.OrderInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author eddie.lee
 * @date 2022-02-07 22:43
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {

//  private RestTemplate restTemplate = new RestTemplate();

  /**
   *  初始, 在没有配置 UserDetailsServiceImpl：  @AuthenticationPrincipal String username
   *  中期：  @AuthenticationPrincipal User user
   *  后期，使用Spring表达式：  @AuthenticationPrincipal(expression = "#this.id") Long userId
   */
  @PostMapping
  public OrderInfo create(@RequestBody OrderInfo info, @AuthenticationPrincipal(expression = "#this.id") Long userId) {
    log.info("user.id is {}", userId);
    return info;
  }

  @GetMapping("/{id}")
  public OrderInfo getInfo(@PathVariable Long id) {
    log.info("orderId is {}", id);
    return new OrderInfo();
  }

}
