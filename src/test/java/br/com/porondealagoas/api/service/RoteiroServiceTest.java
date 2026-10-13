package br.com.porondealagoas.api.service;

import br.com.porondealagoas.api.dto.GerarRoteiroRequestDto;
import br.com.porondealagoas.api.dto.RoteiroResponseDto;
import br.com.porondealagoas.api.entity.Destino;
import br.com.porondealagoas.api.entity.Roteiro;
import br.com.porondealagoas.api.entity.enums.CategoriaDestino;
import br.com.porondealagoas.api.entity.enums.StatusPublicacao;
import br.com.porondealagoas.api.mapper.ApiMapper;
import br.com.porondealagoas.api.repository.DestinoRepository;
import br.com.porondealagoas.api.repository.RoteiroRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoteiroServiceTest {

    @Mock
    private DestinoRepository destinoRepository;

    @Mock
    private RoteiroRepository roteiroRepository;

    private RoteiroService roteiroService;

    @BeforeEach
    void setup() {
        roteiroService = new RoteiroService(destinoRepository, roteiroRepository, new ApiMapper());
    }

    @Test
    void deveGerarRoteiroSemRepetirDestinoEComExplicacaoDeCustosDesconhecidos() {
        GerarRoteiroRequestDto request = new GerarRoteiroRequestDto(
                2,
                new BigDecimal("200.00"),
                List.of(CategoriaDestino.PRAIA),
                List.of("Maceió")
        );

        Destino destinoComCusto = criarDestino(1L, "Praia do Francês", new BigDecimal("80.00"), 180, "Maceió");
        Destino destinoSemCusto = criarDestino(2L, "Praia de Pajuçara", null, 120, "Maceió");

        when(destinoRepository.findPublicadosComFiltros(eq(StatusPublicacao.PUBLICADO), any(), any()))
                .thenReturn(List.of(destinoComCusto, destinoSemCusto));

        when(roteiroRepository.save(any(Roteiro.class))).thenAnswer(invocation -> {
            Roteiro roteiro = invocation.getArgument(0);
            roteiro.setId(10L);
            roteiro.setCriadoEm(OffsetDateTime.now());
            return roteiro;
        });

        RoteiroResponseDto response = roteiroService.gerar(request);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.itens()).hasSize(2);
        assertThat(response.itens().stream().map(i -> i.destino().id())).doesNotHaveDuplicates();
        assertThat(response.explicacao()).contains("custo desconhecido");
    }

    @Test
    void deveInformarQuandoNaoHouverDestinosSuficientes() {
        GerarRoteiroRequestDto request = new GerarRoteiroRequestDto(
                3,
                new BigDecimal("100.00"),
                List.of(CategoriaDestino.CULTURA),
                null
        );

        Destino unicoDestino = criarDestino(1L, "Museu X", new BigDecimal("20.00"), 90, "Penedo");

        when(destinoRepository.findPublicadosComFiltros(eq(StatusPublicacao.PUBLICADO), any(), any()))
                .thenReturn(List.of(unicoDestino));

        when(roteiroRepository.save(any(Roteiro.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RoteiroResponseDto response = roteiroService.gerar(request);

        assertThat(response.itens()).hasSize(1);
        assertThat(response.explicacao()).contains("Não há destinos suficientes");
    }

    private Destino criarDestino(Long id,
                                 String nome,
                                 BigDecimal custo,
                                 Integer duracao,
                                 String municipio) {
        Destino destino = new Destino();
        destino.setId(id);
        destino.setNome(nome);
        destino.setDescricao("Descrição");
        destino.setMunicipio(municipio);
        destino.setCategoria(CategoriaDestino.PRAIA);
        destino.setCustoEstimado(custo);
        destino.setDuracaoEstimadaMinutos(duracao);
        destino.setStatusPublicacao(StatusPublicacao.PUBLICADO);
        return destino;
    }
}
