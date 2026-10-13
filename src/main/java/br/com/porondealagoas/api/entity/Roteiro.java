package br.com.porondealagoas.api.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roteiros")
public class Roteiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false)
    private Integer quantidadeDias;

    @Column(precision = 10, scale = 2)
    private BigDecimal orcamentoEstimado;

    @Column(nullable = false, length = 400)
    private String interessesSelecionados;

    @Column(nullable = false, length = 2000)
    private String explicacao;

    @Column(nullable = false)
    private OffsetDateTime criadoEm;

    @OneToMany(mappedBy = "roteiro", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemRoteiro> itens = new ArrayList<>();

    public void adicionarItem(ItemRoteiro item) {
        item.setRoteiro(this);
        this.itens.add(item);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getQuantidadeDias() {
        return quantidadeDias;
    }

    public void setQuantidadeDias(Integer quantidadeDias) {
        this.quantidadeDias = quantidadeDias;
    }

    public BigDecimal getOrcamentoEstimado() {
        return orcamentoEstimado;
    }

    public void setOrcamentoEstimado(BigDecimal orcamentoEstimado) {
        this.orcamentoEstimado = orcamentoEstimado;
    }

    public String getInteressesSelecionados() {
        return interessesSelecionados;
    }

    public void setInteressesSelecionados(String interessesSelecionados) {
        this.interessesSelecionados = interessesSelecionados;
    }

    public String getExplicacao() {
        return explicacao;
    }

    public void setExplicacao(String explicacao) {
        this.explicacao = explicacao;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(OffsetDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<ItemRoteiro> getItens() {
        return itens;
    }

    public void setItens(List<ItemRoteiro> itens) {
        this.itens = itens;
    }
}
