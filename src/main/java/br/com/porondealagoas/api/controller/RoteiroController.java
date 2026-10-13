package br.com.porondealagoas.api.controller;

import br.com.porondealagoas.api.dto.GerarRoteiroRequestDto;
import br.com.porondealagoas.api.dto.RoteiroResponseDto;
import br.com.porondealagoas.api.service.RoteiroService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roteiros")
public class RoteiroController {

    private final RoteiroService roteiroService;

    public RoteiroController(RoteiroService roteiroService) {
        this.roteiroService = roteiroService;
    }

    @PostMapping("/gerar")
    public RoteiroResponseDto gerar(@Valid @RequestBody GerarRoteiroRequestDto request) {
        return roteiroService.gerar(request);
    }

    @GetMapping("/{id}")
    public RoteiroResponseDto buscarPorId(@PathVariable Long id) {
        return roteiroService.buscarPorId(id);
    }
}
