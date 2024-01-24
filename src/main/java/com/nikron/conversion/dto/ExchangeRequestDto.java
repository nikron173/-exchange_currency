package com.nikron.conversion.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class ExchangeRequestDto {
    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private BigDecimal amount;
}
