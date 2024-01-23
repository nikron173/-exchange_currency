package com.nikron.conversion.controller;

import com.nikron.conversion.mapper.ExchangeRatesMapper;
import com.nikron.conversion.model.ExchangeRates;
import com.nikron.conversion.service.ExchangeRatesService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = "/exchangeRates")
public class ExchangeRatesController extends HttpServlet {

    private final ExchangeRatesMapper mapper = new ExchangeRatesMapper();

    private final ExchangeRatesService service = new ExchangeRatesService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Content-Type", "application/json; charset=utf-8");
        var writer = resp.getWriter();
        Optional<List<ExchangeRates>> exchangeRates = service.findAll();
        if (exchangeRates.isEmpty()){
            writer.println("\"{}\"");
        } else {
            for (ExchangeRates ex : exchangeRates.get()) {
                writer.println(mapper.exchangeRatesToJson(ex));
            }
        }
        writer.close();
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
