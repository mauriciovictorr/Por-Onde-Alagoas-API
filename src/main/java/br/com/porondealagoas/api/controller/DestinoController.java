package br.com.porondealagoas.api.controller;

import br.com.porondealagoas.api.dto.DestinoResponseDto;
import br.com.porondealagoas.api.entity.enums.CategoriaDestino;
import br.com.porondealagoas.api.service.DestinoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/destinos")
public class DestinoController {

    private final DestinoService destinoService;

    public DestinoController(DestinoService destinoService) {
        this.destinoService = destinoService;
    }

    @GetMapping
    public Page<DestinoResponseDto> listar(@RequestParam(required = false) String municipio,
                                           @RequestParam(required = false) CategoriaDestino categoria,
                                           @PageableDefault(size = 20) Pageable pageable) {
        return destinoService.listar(municipio, categoria, pageable);
    }

    @GetMapping("/{id}")
    public DestinoResponseDto buscarPorId(@PathVariable Long id) {
        return destinoService.buscarPorId(id);
    }
}
