package com.nikron.conversion.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikron.conversion.exception.BadRequestException;
import com.nikron.conversion.model.Currency;
import com.nikron.conversion.model.ExchangeRates;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");
        if (Objects.isNull(baseCurrencyCode) || Objects.isNull(targetCurrencyCode) || Objects.isNull(rate)) return false;
        if (baseCurrencyCode.isBlank() || targetCurrencyCode.isBlank() || rate.isBlank()) return false;
        return checkBigDecimal(rate);
    }

    public boolean checkBigDecimal(String number) {
        try {
            BigDecimal bigDecimalRate = new BigDecimal(number);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Не верно задано число " + number,
                    HttpServletResponse.SC_BAD_REQUEST);
        }
        return true;
    }

    public boolean checkParamExchange(HttpServletRequest req) {
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amount = req.getParameter("amount");
        if (Objects.isNull(from) || Objects.isNull(to) || Objects.isNull(amount)) return false;
        if (from.isBlank() || to.isBlank() || amount.isBlank()) return false;
        return checkBigDecimal(amount);
    }
}
