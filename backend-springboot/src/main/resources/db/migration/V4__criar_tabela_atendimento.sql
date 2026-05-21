CREATE TABLE atendimento (
    id BIGSERIAL PRIMARY KEY,

    paciente_id BIGINT NOT NULL,

    data_atendimento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    queixa_principal TEXT,

    evolucao_clinica TEXT,

    procedimento_realizado TEXT,

    observacoes TEXT,

    CONSTRAINT fk_atendimento_paciente
        FOREIGN KEY (paciente_id)
        REFERENCES paciente(id)
);