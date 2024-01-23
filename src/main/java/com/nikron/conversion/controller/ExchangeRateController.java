package com.nikron.conversion.controller;

import com.nikron.conversion.exception.BadRequestException;
import com.nikron.conversion.mapper.ExchangeRatesMapper;
import com.nikron.conversion.model.ExchangeRates;
import com.nikron.conversion.service.CurrencyService;
import com.nikron.conversion.service.ExchangeRatesService;
import com.nikron.conversion.util.UriStringMatch;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet(urlPatterns = "/exchangeRate/*")
public class ExchangeRateController extends HttpServlet {

    private final ExchangeRatesService ratesService = new ExchangeRatesService();
    private final CurrencyService currencyService = new CurrencyService();
    private final ExchangeRatesMapper mapper = new ExchangeRatesMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Content-Type", "application/json; charset=utf-8");
        Optional<?> query = UriStringMatch.uriMatch(req.getRequestURI());
        if (query.isEmpty()) {
            throw new BadRequestException("Ошибка запроса uri " + req.getRequestURI());
        } else if (query.get() instanceof String) {

        } else {

        }
    }
}
