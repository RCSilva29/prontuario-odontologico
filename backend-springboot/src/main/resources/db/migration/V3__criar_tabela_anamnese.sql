CREATE TABLE anamnese (
    id BIGSERIAL PRIMARY KEY,

    paciente_id BIGINT NOT NULL UNIQUE,

    hipertensao BOOLEAN NOT NULL DEFAULT FALSE,
    diabetes BOOLEAN NOT NULL DEFAULT FALSE,
    alergias TEXT,
    medicamentos TEXT,
    fumante BOOLEAN NOT NULL DEFAULT FALSE,
    gravida BOOLEAN NOT NULL DEFAULT FALSE,
    observacoes TEXT,

    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_anamnese_paciente
        FOREIGN KEY (paciente_id)
        REFERENCES paciente(id)
);