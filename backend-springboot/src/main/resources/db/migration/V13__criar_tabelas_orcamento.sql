CREATE TABLE orcamento (
    id BIGSERIAL PRIMARY KEY,
    paciente_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    validade_dias INTEGER NOT NULL,
    observacoes TEXT,
    subtotal NUMERIC(12,2) NOT NULL DEFAULT 0,
    desconto NUMERIC(12,2) NOT NULL DEFAULT 0,
    total NUMERIC(12,2) NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL,

    CONSTRAINT fk_orcamento_paciente
        FOREIGN KEY (paciente_id)
        REFERENCES paciente(id),

    CONSTRAINT fk_orcamento_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuario(id)
);

CREATE TABLE orcamento_item (
    id BIGSERIAL PRIMARY KEY,
    orcamento_id BIGINT NOT NULL,
    codigo VARCHAR(30),
    dentes VARCHAR(100),
    procedimento VARCHAR(255) NOT NULL,
    quantidade INTEGER NOT NULL,
    valor_unitario NUMERIC(12,2) NOT NULL,
    subtotal NUMERIC(12,2) NOT NULL,

    CONSTRAINT fk_orcamento_item_orcamento
        FOREIGN KEY (orcamento_id)
        REFERENCES orcamento(id)
        ON DELETE CASCADE
);