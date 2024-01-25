package com.nikron.conversion.filter;

import com.nikron.conversion.dto.ExceptionDto;
import com.nikron.conversion.exception.ApplicationException;
import com.nikron.conversion.util.JsonResponce;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebFilter(urlPatterns = "/*")
public class ApplicationFilter extends HttpFilter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");

        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            if (e instanceof ApplicationException) {
                JsonResponce.jsonResponse(response, new ExceptionDto(e.getMessage()), ((ApplicationException) e).getStatus());
            } else {
                throw new RuntimeException(e);
            }
        }
    }
}
