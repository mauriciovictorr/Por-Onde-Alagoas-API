CREATE TABLE destinos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    descricao VARCHAR(2000) NOT NULL,
    municipio VARCHAR(120) NOT NULL,
    categoria VARCHAR(30) NOT NULL,
    custo_estimado NUMERIC(10,2),
    duracao_estimada_minutos INTEGER,
    horarios_funcionamento VARCHAR(300),
    latitude NUMERIC(10,7),
    longitude NUMERIC(10,7),
    imagem_url VARCHAR(500),
    status_publicacao VARCHAR(20) NOT NULL,
    CONSTRAINT ck_destinos_custo CHECK (custo_estimado IS NULL OR custo_estimado >= 0),
    CONSTRAINT ck_destinos_duracao CHECK (duracao_estimada_minutos IS NULL OR duracao_estimada_minutos > 0)
);

CREATE TABLE roteiros (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    quantidade_dias INTEGER NOT NULL,
    orcamento_estimado NUMERIC(10,2),
    interesses_selecionados VARCHAR(400) NOT NULL,
    explicacao VARCHAR(2000) NOT NULL,
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT ck_roteiros_dias CHECK (quantidade_dias > 0),
    CONSTRAINT ck_roteiros_orcamento CHECK (orcamento_estimado IS NULL OR orcamento_estimado >= 0)
);

CREATE TABLE itens_roteiro (
    id BIGSERIAL PRIMARY KEY,
    roteiro_id BIGINT NOT NULL REFERENCES roteiros(id) ON DELETE CASCADE,
    destino_id BIGINT NOT NULL REFERENCES destinos(id),
    dia_viagem INTEGER NOT NULL,
    ordem_visita INTEGER NOT NULL,
    observacoes VARCHAR(500),
    CONSTRAINT ck_itens_dia CHECK (dia_viagem > 0),
    CONSTRAINT ck_itens_ordem CHECK (ordem_visita > 0),
    CONSTRAINT uq_roteiro_dia_ordem UNIQUE (roteiro_id, dia_viagem, ordem_visita),
    CONSTRAINT uq_roteiro_dia_destino UNIQUE (roteiro_id, dia_viagem, destino_id)
);

CREATE INDEX idx_destinos_municipio ON destinos (municipio);
CREATE INDEX idx_destinos_categoria ON destinos (categoria);
CREATE INDEX idx_destinos_status ON destinos (status_publicacao);
