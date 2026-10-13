package br.com.porondealagoas.api.dto;

import br.com.porondealagoas.api.entity.enums.CategoriaDestino;

import java.math.BigDecimal;

public record DestinoResponseDto(
        Long id,
        String nome,
        String descricao,
        String municipio,
        CategoriaDestino categoria,
        BigDecimal custoEstimado,
        Integer duracaoEstimadaMinutos,
        String horariosFuncionamento,
        BigDecimal latitude,
        BigDecimal longitude,
        String imagemUrl
) {
}
