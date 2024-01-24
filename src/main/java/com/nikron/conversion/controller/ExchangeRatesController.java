package com.nikron.conversion.controller;

import com.nikron.conversion.dto.ExchangeRatesDto;
import com.nikron.conversion.exception.BadRequestException;
import com.nikron.conversion.mapper.ExchangeRatesMapper;
import com.nikron.conversion.model.ExchangeRates;
import com.nikron.conversion.service.ExchangeRatesService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = "/exchangeRates")
public class ExchangeRatesController extends HttpServlet {

    private final ExchangeRatesMapper mapper = new ExchangeRatesMapper();

    private final ExchangeRatesService service = new ExchangeRatesService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!mapper.checkParameter(req)){
            throw new BadRequestException("Не верно заданы параметры для создания обменника валюты",
                    HttpServletResponse.SC_BAD_REQUEST);
        }
        ExchangeRatesDto dto = new ExchangeRatesDto();
        dto.setBaseCurrencyCode(req.getParameter("baseCurrencyCode"));
        dto.setTargetCurrencyCode(req.getParameter("targetCurrencyCode"));
        dto.setRate(new BigDecimal(req.getParameter("rate")));
        Optional<ExchangeRates> exchangeRates = service.addExchangeRates(dto);
        if (exchangeRates.isPresent()){
            resp.sendRedirect("/exchangeRate/" +
                    exchangeRates.get().getBaseCurrency().getCode() +
                    exchangeRates.get().getTargetCurrency().getCode());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var writer = resp.getWriter();
        Optional<List<ExchangeRates>> exchangeRates = service.findAll();
        if (exchangeRates.isEmpty()) {
            writer.println(Optional.empty());
        } else {
            for (ExchangeRates ex : exchangeRates.get()) {
                writer.println(mapper.exchangeRatesToJson(ex));
            }
        }
        writer.close();
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
