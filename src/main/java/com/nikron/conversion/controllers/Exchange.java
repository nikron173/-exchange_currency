package com.nikron.conversion.controllers;

import com.nikron.conversion.dao.DataDB;
import com.nikron.conversion.exceptions.MyException;
import com.nikron.conversion.model.ExchangeVolute;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "/exchange", urlPatterns = "/exchange")
public class Exchange extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        if (req.getQueryString().matches("from=\\w+&to=\\w+&amount=\\d+(\\.\\d+)?")) {
            ExchangeVolute exchangeVolute = null;
            try {
                exchangeVolute = DataDB.getExchange(req.getParameter("from")+req.getParameter("to"));
            } catch (MyException e){
                resp.getWriter().print(MyException.jsonExceptionBuild("error", e.getMessage()));
                resp.setStatus(500);
            }
            double amount;
            try {
                amount = Double.parseDouble(req.getParameter("amount"));
            } catch (NumberFormatException e){
                resp.getWriter().print(MyException.jsonExceptionBuild("message", "error number amount"));
                resp.setStatus(400);
                return;
            }
            if (exchangeVolute == null) {
                resp.getWriter().print(MyException.jsonExceptionBuild("message", "not found base_currency or target_currency"));
                resp.setStatus(400);
                return;
            }
            double convertedAmount = exchangeVolute.getRates()*amount;
            PrintWriter writer = resp.getWriter();
            JsonObject jsonObject = Json.createObjectBuilder().add("baseCurrency", exchangeVolute.getBase().convertJson())
                            .add("targetCurrency", exchangeVolute.getTarget().convertJson())
                                    .add("rate", exchangeVolute.getRates())
                                            .add("amount", amount)
                                                    .add("convertedAmount", convertedAmount).build();
            writer.print(jsonObject.toString());
            resp.setStatus(200);
            writer.close();
        } else {
            resp.sendError(400);
        }
    }
}
