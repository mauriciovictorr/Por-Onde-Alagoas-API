package br.com.porondealagoas.api.service;

import br.com.porondealagoas.api.dto.GerarRoteiroRequestDto;
import br.com.porondealagoas.api.dto.RoteiroResponseDto;
import br.com.porondealagoas.api.entity.Destino;
import br.com.porondealagoas.api.entity.ItemRoteiro;
import br.com.porondealagoas.api.entity.Roteiro;
import br.com.porondealagoas.api.entity.enums.CategoriaDestino;
import br.com.porondealagoas.api.entity.enums.StatusPublicacao;
import br.com.porondealagoas.api.exception.NotFoundException;
import br.com.porondealagoas.api.mapper.ApiMapper;
import br.com.porondealagoas.api.repository.DestinoRepository;
import br.com.porondealagoas.api.repository.RoteiroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoteiroService {

    private static final int LIMITE_MINUTOS_POR_DIA = 480;

    private final DestinoRepository destinoRepository;
    private final RoteiroRepository roteiroRepository;
    private final ApiMapper apiMapper;

    public RoteiroService(DestinoRepository destinoRepository,
                          RoteiroRepository roteiroRepository,
                          ApiMapper apiMapper) {
        this.destinoRepository = destinoRepository;
        this.roteiroRepository = roteiroRepository;
        this.apiMapper = apiMapper;
    }

    @Transactional
    public RoteiroResponseDto gerar(GerarRoteiroRequestDto request) {
        List<String> municipiosFiltrados = normalizarMunicipios(request.municipiosInteresse());

        List<Destino> candidatos = destinoRepository.findPublicadosComFiltros(
                StatusPublicacao.PUBLICADO,
                request.interesses(),
                municipiosFiltrados.isEmpty() ? null : municipiosFiltrados
        );

        if (candidatos.isEmpty()) {
            Roteiro vazio = construirRoteiroBase(request, "Roteiro com disponibilidade limitada");
            vazio.setExplicacao("Nenhum destino publicado foi encontrado para os filtros informados.");
            return apiMapper.toRoteiroResponse(roteiroRepository.save(vazio));
        }

        List<Destino> ordenados = candidatos.stream()
                .sorted(Comparator.comparing((Destino d) -> d.getCustoEstimado() == null)
                        .thenComparing(Destino::getMunicipio, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(Destino::getDuracaoEstimadaMinutos, Comparator.nullsLast(Integer::compareTo)))
                .toList();

        BigDecimal custoConhecidoAcumulado = BigDecimal.ZERO;
        int destinosComCustoDesconhecido = 0;

        Set<Long> idsSelecionados = new LinkedHashSet<>();
        List<Destino> selecionados = new ArrayList<>();

        for (Destino destino : ordenados) {
            if (idsSelecionados.contains(destino.getId())) {
                continue;
            }

            BigDecimal custo = destino.getCustoEstimado();
            if (custo != null) {
                BigDecimal novoTotal = custoConhecidoAcumulado.add(custo);
                if (novoTotal.compareTo(request.orcamentoDisponivel()) > 0 && !selecionados.isEmpty()) {
                    continue;
                }
                custoConhecidoAcumulado = novoTotal;
            } else {
                destinosComCustoDesconhecido++;
            }

            idsSelecionados.add(destino.getId());
            selecionados.add(destino);
        }

        Roteiro roteiro = construirRoteiroBase(request, "Roteiro personalizado Alagoas");
        distribuirItens(roteiro, selecionados, request.quantidadeDias());
        roteiro.setExplicacao(construirExplicacao(request, selecionados.size(), custoConhecidoAcumulado, destinosComCustoDesconhecido));

        return apiMapper.toRoteiroResponse(roteiroRepository.save(roteiro));
    }

    @Transactional(readOnly = true)
    public RoteiroResponseDto buscarPorId(Long id) {
        Roteiro roteiro = roteiroRepository.findWithItensById(id)
                .orElseThrow(() -> new NotFoundException("Roteiro não encontrado."));
        return apiMapper.toRoteiroResponse(roteiro);
    }

    private Roteiro construirRoteiroBase(GerarRoteiroRequestDto request, String nome) {
        Roteiro roteiro = new Roteiro();
        roteiro.setNome(nome);
        roteiro.setQuantidadeDias(request.quantidadeDias());
        roteiro.setOrcamentoEstimado(request.orcamentoDisponivel());
        roteiro.setInteressesSelecionados(formatarInteresses(request.interesses()));
        roteiro.setCriadoEm(OffsetDateTime.now());
        return roteiro;
    }

    private void distribuirItens(Roteiro roteiro, List<Destino> destinos, int quantidadeDias) {
        int[] minutosPorDia = new int[quantidadeDias];
        int[] ordemPorDia = new int[quantidadeDias];
        int diaAtual = 0;

        for (Destino destino : destinos) {
            int duracao = destino.getDuracaoEstimadaMinutos() != null ? destino.getDuracaoEstimadaMinutos() : 120;
            int tentativas = 0;

            while (tentativas < quantidadeDias
                    && minutosPorDia[diaAtual] + duracao > LIMITE_MINUTOS_POR_DIA
                    && minutosPorDia[diaAtual] > 0) {
                diaAtual = (diaAtual + 1) % quantidadeDias;
                tentativas++;
            }

            minutosPorDia[diaAtual] += duracao;
            ordemPorDia[diaAtual]++;

            ItemRoteiro item = new ItemRoteiro();
            item.setDestino(destino);
            item.setDiaViagem(diaAtual + 1);
            item.setOrdemVisita(ordemPorDia[diaAtual]);
            item.setObservacoes("Seleção baseada em categoria, município e orçamento estimado.");
            roteiro.adicionarItem(item);

            diaAtual = (diaAtual + 1) % quantidadeDias;
        }
    }

    private String construirExplicacao(GerarRoteiroRequestDto request,
                                       int totalSelecionados,
                                       BigDecimal custoConhecido,
                                       int destinosComCustoDesconhecido) {
        StringBuilder explicacao = new StringBuilder();
        explicacao.append("Foram selecionados ").append(totalSelecionados)
                .append(" destinos com base nos interesses informados: ")
                .append(formatarInteresses(request.interesses())).append(". ");

        explicacao.append("O custo conhecido estimado foi de R$ ").append(custoConhecido)
                .append(" para um orçamento informado de R$ ").append(request.orcamentoDisponivel()).append(". ");

        if (destinosComCustoDesconhecido > 0) {
            explicacao.append(destinosComCustoDesconhecido)
                    .append(" destino(s) possuem custo desconhecido e não entram no cálculo estimado. ");
        }

        if (totalSelecionados < request.quantidadeDias()) {
            explicacao.append("Não há destinos suficientes para preencher todos os dias da viagem sem repetição. ");
        }

        explicacao.append("A ordem considera duração estimada e agrupamento simples por município, sem otimização geográfica por mapa.");

        return explicacao.toString();
    }

    private List<String> normalizarMunicipios(List<String> municipiosInteresse) {
        if (municipiosInteresse == null || municipiosInteresse.isEmpty()) {
            return List.of();
        }
        return municipiosInteresse.stream()
                .filter(m -> m != null && !m.isBlank())
                .map(m -> m.toLowerCase(Locale.ROOT).trim())
                .distinct()
                .toList();
    }

    private String formatarInteresses(List<CategoriaDestino> interesses) {
        return interesses.stream().map(Enum::name).collect(Collectors.joining(", "));
    }
}
