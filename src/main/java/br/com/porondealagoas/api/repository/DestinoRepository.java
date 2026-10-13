package br.com.porondealagoas.api.repository;

import br.com.porondealagoas.api.entity.Destino;
import br.com.porondealagoas.api.entity.enums.CategoriaDestino;
import br.com.porondealagoas.api.entity.enums.StatusPublicacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface DestinoRepository extends JpaRepository<Destino, Long> {

    @Query("""
            select d from Destino d
            where d.statusPublicacao = :status
              and (:municipio is null or lower(d.municipio) = lower(:municipio))
              and (:categoria is null or d.categoria = :categoria)
            """)
    Page<Destino> buscarComFiltros(@Param("status") StatusPublicacao status,
                                   @Param("municipio") String municipio,
                                   @Param("categoria") CategoriaDestino categoria,
                                   Pageable pageable);

    @Query("""
            select d from Destino d
            where d.statusPublicacao = :status
              and (:categorias is null or d.categoria in :categorias)
              and (:municipios is null or lower(d.municipio) in :municipios)
            """)
    List<Destino> findPublicadosComFiltros(@Param("status") StatusPublicacao status,
                                           @Param("categorias") Collection<CategoriaDestino> categorias,
                                           @Param("municipios") Collection<String> municipios);
}
