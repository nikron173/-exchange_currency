package com.nikron.conversion.controller;

import com.nikron.conversion.exception.BadRequestException;
import com.nikron.conversion.mapper.ExchangeRatesMapper;
import com.nikron.conversion.model.ExchangeRates;
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

    private final ExchangeRatesService service = ExchangeRatesService.getInstanceService();
    private final ExchangeRatesMapper mapper = ExchangeRatesMapper.getInstanceMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Optional<?> query = UriStringMatch.uriMatch(req.getRequestURI());
        if (query.isEmpty()) {
            throw new BadRequestException("Ошибка запроса uri " + req.getRequestURI(),
                    HttpServletResponse.SC_BAD_REQUEST);
        }
        if (query.get() instanceof String) {
            ExchangeRates exchangeRates = service.findByCode(((String) query.get()).toUpperCase());
            var writer = resp.getWriter();
            writer.println(mapper.exchangeRatesToJson(exchangeRates));
            writer.close();
        } else {
            ExchangeRates exchangeRates = service.findById((Integer) query.get());
            var writer = resp.getWriter();
            writer.println(mapper.exchangeRatesToJson(exchangeRates));
            writer.close();
        }
    }


    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Optional<?> query = UriStringMatch.uriMatch(req.getRequestURI());
        if (query.isEmpty() || Objects.isNull(req.getParameter("rate")) ||
                !mapper.checkBigDecimal(req.getParameter("rate"))) {
            throw new BadRequestException("Не задан rate (double) или ошибка в запросе uri " + req.getRequestURI(),
                    HttpServletResponse.SC_BAD_REQUEST);
        }
        ExchangeRates updateExchangeRates =
                service.change((String) query.get(), new BigDecimal(req.getParameter("rate")));
        JsonResponce.jsonResponse(resp, updateExchangeRates, HttpServletResponse.SC_OK);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equals("PATCH")) {
            this.doPatch(req, resp);
        }
        super.service(req, resp);
    }
}
