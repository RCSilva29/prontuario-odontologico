CREATE TABLE odontograma (
    id BIGSERIAL PRIMARY KEY,

    paciente_id BIGINT NOT NULL,

    numero_dente VARCHAR(10) NOT NULL,

    status VARCHAR(100) NOT NULL,

    observacao TEXT,

    data_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_odontograma_paciente
        FOREIGN KEY (paciente_id)
        REFERENCES paciente(id)
);