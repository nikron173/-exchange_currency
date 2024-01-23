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
    private long baseCurrencyId;
    private long targetCurrencyId;
    private BigDecimal rate;
}
