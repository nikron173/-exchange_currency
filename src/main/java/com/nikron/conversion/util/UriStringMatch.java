package com.nikron.conversion.util;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public class UriStringMatch {

    public static Optional<?> uriMatch(String uri){
        if (uri.matches("^/\\w+/[0-9]+")) {
            Long num = Long.parseLong(uri.replaceAll("/\\w+/", ""));
            return Optional.of(num);
        }
        if (uri.matches("^/\\w+/[a-zA-Z]+")) {
            String code = uri.replaceAll("/\\w+/", "");
            return Optional.of(code);
        }
        return Optional.empty();
    }
}
