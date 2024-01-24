package com.nikron.conversion.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public final class JsonResponce {
    private JsonResponce() {
    }

    public static <T> void jsonResponse(HttpServletResponse res, T object, int status) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String message = mapper.writeValueAsString(object);
        var writer = res.getWriter();

        res.setStatus(status);
        writer.println(message);
        writer.close();
    }
}
