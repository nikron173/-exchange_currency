package com.nikron.conversion.controller;

import com.nikron.conversion.dto.ExchangeRatesDto;
import com.nikron.conversion.dto.ExchangeRequestDto;
import com.nikron.conversion.exception.BadRequestException;
import com.nikron.conversion.mapper.ExchangeRatesMapper;
import com.nikron.conversion.service.ExchangeRatesService;
import com.nikron.conversion.util.JsonResponce;
import jakarta.json.Json;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(urlPatterns = "/exchange")
public class ExchangeController extends HttpServlet {

    private final ExchangeRatesService service = new ExchangeRatesService();
    private final ExchangeRatesMapper mapper = new ExchangeRatesMapper();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!mapper.checkParamExchange(req)) {
            throw new BadRequestException("Не верно заданы параметры для обменника",
                    HttpServletResponse.SC_BAD_REQUEST);
        }
        ExchangeRequestDto dto = new ExchangeRequestDto();
        dto.setBaseCurrencyCode(req.getParameter("from"));
        dto.setTargetCurrencyCode(req.getParameter("to"));
        dto.setAmount(new BigDecimal(req.getParameter("amount")));

        JsonResponce.jsonResponse(resp, service.exchange(dto), HttpServletResponse.SC_OK);
    }
}
