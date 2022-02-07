package com.edcode.commerce.controller;

import com.edcode.commerce.entity.OrderInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

  @PostMapping
  public OrderInfo create(@RequestBody OrderInfo info, @AuthenticationPrincipal String username) {
    log.info("username is {}", username);
    return info;
  }

}
