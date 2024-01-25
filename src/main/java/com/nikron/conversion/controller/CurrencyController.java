package com.nikron.conversion.controller;

import com.nikron.conversion.exception.BadRequestException;
import com.nikron.conversion.mapper.CurrencyMapper;
import com.nikron.conversion.model.Currency;
import com.nikron.conversion.service.CurrencyService;
import com.nikron.conversion.util.UriStringMatch;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet(urlPatterns = "/currency/*")
public class CurrencyController extends HttpServlet {

    private final CurrencyService service = CurrencyService.getInstanceService();
    private final CurrencyMapper mapper = CurrencyMapper.getInstanceMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Optional<?> query = UriStringMatch.uriMatch(req.getRequestURI());
        var writer = resp.getWriter();
        if (query.isEmpty()) {
            throw new BadRequestException("Uri " + req.getRequestURI() + " not correctly.",
                    HttpServletResponse.SC_BAD_REQUEST);
        }
        if (query.get() instanceof Long) {
            Long id = (Long) query.get();
            Currency currency = service.findById(id);
            resp.setStatus(HttpServletResponse.SC_OK);
            writer.println(mapper.currencyToJson(currency));
        } else if (query.get() instanceof String) {
            String code = (String) query.get();
            Currency currency = service.findByCode(code);
            resp.setStatus(HttpServletResponse.SC_OK);
            writer.println(mapper.currencyToJson(currency));
        }
        writer.close();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Optional<?> query = UriStringMatch.uriMatch(req.getRequestURI());
        if (query.isEmpty()) {
            throw new BadRequestException("Uri " + req.getRequestURI() + " not correctly.",
                    HttpServletResponse.SC_BAD_REQUEST);
        }
        if (query.get() instanceof Long) {
            Long id = (Long) query.get();
            service.delete(id);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.sendRedirect("/currencies");
        } else if (query.get() instanceof String) {
            String code = (String) query.get();
            service.delete(code);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.sendRedirect("/currencies");
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //надо доделать
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equals("PATCH")) {
            this.doPatch(req, resp);
        }
        super.service(req, resp);
    }
}
