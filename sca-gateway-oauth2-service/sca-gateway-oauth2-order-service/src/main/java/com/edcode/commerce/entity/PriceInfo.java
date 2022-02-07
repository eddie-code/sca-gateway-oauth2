package com.edcode.commerce.entity;

import java.math.BigDecimal;
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
@AllArgsConstructor
@NoArgsConstructor
public class PriceInfo {

  private Long id;

  private BigDecimal price;

}
