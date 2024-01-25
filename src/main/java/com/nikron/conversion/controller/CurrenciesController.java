package com.nikron.conversion.controller;

import com.nikron.conversion.dto.CurrencyRequestDto;
import com.nikron.conversion.exception.BadRequestException;
import com.nikron.conversion.mapper.CurrencyMapper;
import com.nikron.conversion.model.Currency;
import com.nikron.conversion.service.CurrencyService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
@WebServlet(urlPatterns = "/currencies")
public class CurrenciesController extends HttpServlet {
    private final CurrencyService service = CurrencyService.getInstanceService();
    private final CurrencyMapper mapper = CurrencyMapper.getInstanceMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var writer = resp.getWriter();
        for (Currency c : service.findAll()) {
            writer.println(mapper.currencyToJson(c));
        }
        writer.close();
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!mapper.checkParameter(req)) {
            throw new BadRequestException("Задайте все параметры валюты (code (1-3 символов), " +
                    "fullName (от 5 символов до 255), sign (от 1-3 символов)", HttpServletResponse.SC_BAD_REQUEST);
        }
        CurrencyRequestDto dto = new CurrencyRequestDto(
                req.getParameter("code"),
                req.getParameter("fullName"),
                req.getParameter("sign")
        );
        service.save(mapper.dtoToCurrency(dto));
        resp.sendRedirect("/currencies");
    }
}
