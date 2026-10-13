package br.com.porondealagoas.api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record RoteiroResponseDto(
        Long id,
        String nome,
        Integer quantidadeDias,
        BigDecimal orcamentoEstimado,
        String explicacao,
        OffsetDateTime criadoEm,
        List<ItemRoteiroResponseDto> itens
) {
}
