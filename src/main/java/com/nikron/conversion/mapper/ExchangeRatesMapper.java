package com.nikron.conversion.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikron.conversion.model.Currency;
import com.nikron.conversion.model.ExchangeRates;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class ExchangeRatesMapper {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ExchangeRates resultSetToExchangeRates(ResultSet rs) throws SQLException {
        return new ExchangeRates(
                rs.getLong(1),
                new Currency(rs.getLong(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5)),
                new Currency(rs.getLong(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getString(9)),
                rs.getBigDecimal(10)
        );
    }

    public String exchangeRatesToJson(ExchangeRates exchangeRates) throws JsonProcessingException {
        return objectMapper.writeValueAsString(exchangeRates);
    }

    public boolean checkParameter(HttpServletRequest req) {
        String baseCurrencyId = req.getParameter("baseCurrencyId");
        String targetCurrencyId = req.getParameter("targetCurrencyId");
        String rate = req.getParameter("rate");
        if (Objects.isNull(baseCurrencyId) || Objects.isNull(targetCurrencyId)
                || Objects.isNull(rate)) return false;
        try {
            Long base_id = Long.parseLong(baseCurrencyId);
            Long target_id = Long.parseLong(targetCurrencyId);
            BigDecimal bigDecimalRate = new BigDecimal(rate);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка в передаче параметров");
        }
        return true;
    }
}
