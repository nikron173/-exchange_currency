package com.nikron.conversion.controllers;

import com.nikron.conversion.dao.DataDB;
import com.nikron.conversion.exceptions.MyException;
import com.nikron.conversion.model.Currency;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "OneCurrency", urlPatterns = "/currency/*")
public class OneCurrency extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getRequestURI().matches("/currency/\\w+$")) {
            String code = req.getPathInfo().split("/")[1];
            Currency currency = null;
            try {
                currency = DataDB.getCurrency(code);
            } catch (MyException e){
                resp.getWriter().print(MyException.jsonExceptionBuild("error", e.getMessage()));
                resp.setStatus(500);
            }
            if (currency == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.sendError(400);
            } else {
                resp.getWriter().print(currency.convertJson());
                resp.setStatus(HttpServletResponse.SC_OK);
            }
        } else {
            resp.setStatus(400);
            resp.sendError(404);
        }
    }
}
