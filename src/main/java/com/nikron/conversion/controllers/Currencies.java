package com.nikron.conversion.controllers;

import com.nikron.conversion.dao.DataDB;
import com.nikron.conversion.exceptions.MyException;
import com.nikron.conversion.model.Currency;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "Currencies", value = "/currencies")
public class Currencies extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        List<Currency> currencies = null;
        try {
            currencies = DataDB.getCurrencies();
        } catch (MyException e){
            resp.getWriter().print(MyException.jsonExceptionBuild("error", e.getMessage()));
            resp.setStatus(500);
        }
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        assert currencies != null;
        for (Currency currency : currencies){
            arrayBuilder.add(currency.convertJson());
        }
        resp.getWriter().print(arrayBuilder.build());
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        if (req.getParameter("code") == null || req.getParameter("name") == null
        || req.getParameter("sing") == null) {
            resp.sendError(400);
            return;
        }
        Currency currency = new Currency(req.getParameter("code"),
                req.getParameter("name"), req.getParameter("sing"));
        try {
            DataDB.addCurrency(currency);
        } catch (MyException e){
            resp.getWriter().print(MyException.jsonExceptionBuild("error", e.getMessage()));
            resp.setStatus(500);
            return;
        }
        resp.sendRedirect("/currencies");
    }
}
