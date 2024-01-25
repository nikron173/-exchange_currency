package com.nikron.conversion.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikron.conversion.dto.CurrencyRequestDto;
import com.nikron.conversion.model.Currency;
import jakarta.servlet.http.HttpServletRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class CurrencyMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final CurrencyMapper INSTANCE_MAPPER = new CurrencyMapper();

    private CurrencyMapper() {
    }

    public static CurrencyMapper getInstanceMapper(){
        return INSTANCE_MAPPER;
    }

    public Currency resultSetToCurrency(ResultSet rs) throws SQLException {
        return new Currency(rs.getInt("id"),
                rs.getString("code"),
                rs.getString("full_name"),
                rs.getString("sign")
        );
    }

    public String currencyToJson(Currency currency) throws JsonProcessingException {
        return objectMapper.writeValueAsString(currency);
    }

    public Currency dtoToCurrency(CurrencyRequestDto dto) {
        Currency currency = new Currency();
        currency.setCode(dto.getCode());
        currency.setSign(dto.getSign());
        currency.setFullName(dto.getFullName());
        return currency;
    }

    public boolean checkParameter(HttpServletRequest req) {
        String code = req.getParameter("code");
        if (Objects.isNull(code) || code.length() < 1 || code.length() > 5 || code.isBlank()) return false;
        String fullName = req.getParameter("fullName");
        if (Objects.isNull(fullName) || fullName.length() < 5 ||
                fullName.length() > 254 || fullName.isBlank()) return false;
        String sign = req.getParameter("sign");
        if (Objects.isNull(sign) || sign.length() < 1 || sign.length() > 3 || sign.isBlank()) return false;
        return true;
    }
}
