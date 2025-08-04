package com.jchojdak.recruitmenttask.dto;

public record ErrorResponseDto(
        int status,
        String message
) {}
