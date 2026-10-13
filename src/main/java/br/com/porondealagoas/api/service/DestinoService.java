package br.com.porondealagoas.api.service;

import br.com.porondealagoas.api.dto.DestinoResponseDto;
import br.com.porondealagoas.api.entity.Destino;
import br.com.porondealagoas.api.entity.enums.CategoriaDestino;
import br.com.porondealagoas.api.entity.enums.StatusPublicacao;
import br.com.porondealagoas.api.exception.NotFoundException;
import br.com.porondealagoas.api.mapper.ApiMapper;
import br.com.porondealagoas.api.repository.DestinoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DestinoService {

    private final DestinoRepository destinoRepository;
    private final ApiMapper apiMapper;

    public DestinoService(DestinoRepository destinoRepository, ApiMapper apiMapper) {
        this.destinoRepository = destinoRepository;
        this.apiMapper = apiMapper;
    }

    public Page<DestinoResponseDto> listar(String municipio, CategoriaDestino categoria, Pageable pageable) {
        return destinoRepository.buscarComFiltros(StatusPublicacao.PUBLICADO, municipio, categoria, pageable)
                .map(apiMapper::toDestinoResponse);
    }

    public DestinoResponseDto buscarPorId(Long id) {
        Destino destino = destinoRepository.findById(id)
                .filter(d -> d.getStatusPublicacao() == StatusPublicacao.PUBLICADO)
                .orElseThrow(() -> new NotFoundException("Destino não encontrado."));

        return apiMapper.toDestinoResponse(destino);
    }
}
