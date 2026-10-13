package br.com.porondealagoas.api.dto;

import br.com.porondealagoas.api.entity.enums.CategoriaDestino;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record GerarRoteiroRequestDto(
        @NotNull @Min(1) @Max(30) Integer quantidadeDias,
        @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal orcamentoDisponivel,
        @NotEmpty List<CategoriaDestino> interesses,
        List<String> municipiosInteresse
) {
}
