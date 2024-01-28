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

@WebServlet(urlPatterns = "/exchangeRates")
public class ExchangeRatesController extends HttpServlet {

    private final ExchangeRatesMapper mapper = ExchangeRatesMapper.getInstanceMapper();

    private final ExchangeRatesService service = ExchangeRatesService.getInstanceService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!mapper.checkParameter(req)) {
            throw new BadRequestException("Не верно заданы параметры для создания обменника валюты",
                    HttpServletResponse.SC_BAD_REQUEST);
        }
        ExchangeRatesDto dto = new ExchangeRatesDto();
        dto.setBaseCurrencyCode(req.getParameter("baseCurrencyCode"));
        dto.setTargetCurrencyCode(req.getParameter("targetCurrencyCode"));
        dto.setRate(new BigDecimal(req.getParameter("rate")));
        ExchangeRates exchangeRates = service.addExchangeRates(dto);
        resp.sendRedirect("/exchangeRate/" +
                exchangeRates.getBaseCurrency().getCode() +
                exchangeRates.getTargetCurrency().getCode());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var writer = resp.getWriter();
        List<ExchangeRates> exchangeRates = service.findAll();
        if (exchangeRates.size() == 0) {
            writer.println("{\n\"message\": \"Нет ни одного созданного обменника валют\"\n}");
        } else {
            for (ExchangeRates ex : exchangeRates) {
                writer.println(mapper.exchangeRatesToJson(ex));
            }
        }
        writer.close();
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
