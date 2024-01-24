package com.nikron.conversion.controller;

import com.nikron.conversion.exception.BadRequestException;
import com.nikron.conversion.mapper.ExchangeRatesMapper;
import com.nikron.conversion.model.ExchangeRates;
import com.nikron.conversion.service.CurrencyService;
import com.nikron.conversion.service.ExchangeRatesService;
import com.nikron.conversion.util.JsonResponce;
import com.nikron.conversion.util.UriStringMatch;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

@WebServlet(urlPatterns = "/exchangeRate/*")
public class ExchangeRateController extends HttpServlet {

    private final ExchangeRatesService service = new ExchangeRatesService();
    private final ExchangeRatesMapper mapper = new ExchangeRatesMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Optional<?> query = UriStringMatch.uriMatch(req.getRequestURI());
        if (query.isEmpty()) {
            throw new BadRequestException("Ошибка запроса uri " + req.getRequestURI(),
                    HttpServletResponse.SC_BAD_REQUEST);
        }
        if (query.get() instanceof String) {
            Optional<ExchangeRates> exchangeRates = service.findByCode(((String) query.get()).toUpperCase());
            var writer = resp.getWriter();
            writer.println(exchangeRates.isEmpty() ? Optional.empty() :
                    mapper.exchangeRatesToJson(exchangeRates.get()));
            writer.close();
        } else {
            Optional<ExchangeRates> exchangeRates = service.findById((Integer) query.get());
            var writer = resp.getWriter();
            writer.println(exchangeRates.isEmpty() ? Optional.empty() :
                    mapper.exchangeRatesToJson(exchangeRates.get()));
            writer.close();
        }
    }


    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Optional<?> query = UriStringMatch.uriMatch(req.getRequestURI());
        if (query.isEmpty() || Objects.isNull(req.getParameter("rate")) || !mapper.checkRate(req)) {
            throw new BadRequestException("Не задан rate (double) или ошибка в запросе uri " + req.getRequestURI(),
                    HttpServletResponse.SC_BAD_REQUEST);
        }
        Optional<ExchangeRates> updateExchangeRates =
                service.change((String) query.get(), new BigDecimal(req.getParameter("rate")));
        if (updateExchangeRates.isPresent()){
            JsonResponce.jsonResponse(resp, updateExchangeRates.get(), HttpServletResponse.SC_OK);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equals("PATCH")){
            this.doPatch(req, resp);
        }
        super.service(req,resp);
    }
}
