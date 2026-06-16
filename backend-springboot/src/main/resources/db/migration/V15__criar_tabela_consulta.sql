CREATE TABLE consulta (
    id BIGSERIAL PRIMARY KEY,
    paciente_id BIGINT NOT NULL,
    dentista_id BIGINT NOT NULL,
    data_hora_inicio TIMESTAMP NOT NULL,
    data_hora_fim TIMESTAMP NOT NULL,
    observacao TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'AGENDADA',
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_consulta_paciente
        FOREIGN KEY (paciente_id)
        REFERENCES paciente(id),

    CONSTRAINT fk_consulta_dentista
        FOREIGN KEY (dentista_id)
        REFERENCES usuario(id)
);

CREATE INDEX idx_consulta_periodo
    ON consulta (data_hora_inicio, data_hora_fim);

CREATE INDEX idx_consulta_status
    ON consulta (status);