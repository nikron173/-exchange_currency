package com.nikron.conversion.controllers;

import com.nikron.conversion.dao.DataDB;
import com.nikron.conversion.exceptions.MyException;
import com.nikron.conversion.model.ExchangeVolute;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "exchangeRates", value = "/exchangeRates/*")
public class ExchangeRates extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        if (req.getRequestURI().matches("/exchangeRates(/)?")) {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            int i = 0;

            List<ExchangeVolute> exchangeVoluteList = null;
            try {
                exchangeVoluteList = DataDB.getExchangeList();
            } catch (MyException e){
                resp.getWriter().print(MyException.jsonExceptionBuild("error", e.getMessage()));
                resp.setStatus(500);
            }
            assert exchangeVoluteList != null;
            for (ExchangeVolute exchangeVolute : exchangeVoluteList) {
                arrayBuilder.add(i, exchangeVolute.convertJson());
                i++;
            }
            resp.getWriter().print(arrayBuilder.build().toString());
        } else {
            resp.sendError(404);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        if (req.getRequestURI().matches("/exchangeRates/\\w+")){
            Double rate;
            if (req.getParameter("rate") == null){
                resp.getWriter().print(MyException.jsonExceptionBuild("message", "not found parameter rate"));
                resp.setStatus(400);
                return;
            }
            try {
                rate = Double.parseDouble(req.getParameter("rate"));
                System.out.println(rate);
            } catch (NumberFormatException e){
                resp.getWriter().print(MyException.jsonExceptionBuild("message", "error number rate"));
                resp.setStatus(400);
                return;
            }
            String valutes = req.getRequestURI().split("/")[2];
            ExchangeVolute exchangeVolute = null;
            try {
                exchangeVolute = DataDB.getExchange(valutes);
            } catch (MyException e){
                resp.getWriter().print(MyException.jsonExceptionBuild("error", e.getMessage()));
                resp.setStatus(500);
            }

            if (exchangeVolute != null){
                try {
                    DataDB.changeRate(rate, exchangeVolute);
                    resp.setStatus(200);
                    resp.sendRedirect("/exchangeRates");
                } catch (MyException e){
                    resp.getWriter().print(MyException.jsonExceptionBuild("error", e.getMessage()));
                    resp.setStatus(500);
                }
            } else {
                resp.getWriter().print(MyException.jsonExceptionBuild("message", "not found valutes"));
                resp.setStatus(404);
            }
        } else {
            resp.setStatus(404);
            resp.sendError(404);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if (!method.equals("PATCH")) {
            super.service(req, resp);
        } else {
            this.doPatch(req, resp);
        }
    }
}
