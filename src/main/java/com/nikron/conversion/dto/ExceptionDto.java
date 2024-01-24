package com.nikron.conversion.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ExceptionDto {
    private final String time = String.valueOf(LocalDateTime.now());

    @NonNull
    private String message;
}
