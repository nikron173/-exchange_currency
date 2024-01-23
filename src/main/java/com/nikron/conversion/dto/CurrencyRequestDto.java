package com.nikron.conversion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CurrencyRequestDto {
    private String code;
    private String fullName;
    private String sign;
}
