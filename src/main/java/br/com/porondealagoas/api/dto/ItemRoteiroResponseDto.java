package br.com.porondealagoas.api.dto;

public record ItemRoteiroResponseDto(
        Long id,
        Integer diaViagem,
        Integer ordemVisita,
        String observacoes,
        DestinoResponseDto destino
) {
}
