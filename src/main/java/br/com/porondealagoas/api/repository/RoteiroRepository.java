package br.com.porondealagoas.api.repository;

import br.com.porondealagoas.api.entity.Roteiro;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoteiroRepository extends JpaRepository<Roteiro, Long> {

    @EntityGraph(attributePaths = {"itens", "itens.destino"})
    Optional<Roteiro> findWithItensById(Long id);
}
