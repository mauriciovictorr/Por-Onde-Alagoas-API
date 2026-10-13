package br.com.porondealagoas.api.mapper;

import br.com.porondealagoas.api.dto.DestinoResponseDto;
import br.com.porondealagoas.api.dto.ItemRoteiroResponseDto;
import br.com.porondealagoas.api.dto.RoteiroResponseDto;
import br.com.porondealagoas.api.entity.Destino;
import br.com.porondealagoas.api.entity.ItemRoteiro;
import br.com.porondealagoas.api.entity.Roteiro;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ApiMapper {

    public DestinoResponseDto toDestinoResponse(Destino destino) {
        return new DestinoResponseDto(
                destino.getId(),
                destino.getNome(),
                destino.getDescricao(),
                destino.getMunicipio(),
                destino.getCategoria(),
                destino.getCustoEstimado(),
                destino.getDuracaoEstimadaMinutos(),
                destino.getHorariosFuncionamento(),
                destino.getLatitude(),
                destino.getLongitude(),
                destino.getImagemUrl()
        );
    }

    public RoteiroResponseDto toRoteiroResponse(Roteiro roteiro) {
        List<ItemRoteiroResponseDto> itens = roteiro.getItens().stream()
                .sorted(Comparator.comparing(ItemRoteiro::getDiaViagem).thenComparing(ItemRoteiro::getOrdemVisita))
                .map(this::toItemRoteiroResponse)
                .toList();

        return new RoteiroResponseDto(
                roteiro.getId(),
                roteiro.getNome(),
                roteiro.getQuantidadeDias(),
                roteiro.getOrcamentoEstimado(),
                roteiro.getExplicacao(),
                roteiro.getCriadoEm(),
                itens
        );
    }

    private ItemRoteiroResponseDto toItemRoteiroResponse(ItemRoteiro item) {
        return new ItemRoteiroResponseDto(
                item.getId(),
                item.getDiaViagem(),
                item.getOrdemVisita(),
                item.getObservacoes(),
                toDestinoResponse(item.getDestino())
        );
    }
}
