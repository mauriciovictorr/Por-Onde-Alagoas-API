package br.com.porondealagoas.api.dto;

import java.time.OffsetDateTime;
import java.util.Map;

public record ErrorResponseDto(
        String code,
        String message,
        OffsetDateTime timestamp,
        Map<String, String> details
) {
}
