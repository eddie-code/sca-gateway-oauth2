package com.edcode.commerce.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author eddie.lee
 * @date 2022-02-07 22:42
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfo {

  private Long id;

  private Long productId;

}
