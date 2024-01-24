package com.nikron.conversion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRatesDto {
    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private BigDecimal rate;
}
