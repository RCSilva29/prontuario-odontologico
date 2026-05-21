CREATE TABLE anexo (
    id BIGSERIAL PRIMARY KEY,

    paciente_id BIGINT NOT NULL,

    nome_original VARCHAR(255) NOT NULL,
    nome_arquivo VARCHAR(255) NOT NULL,
    tipo_conteudo VARCHAR(100),
    tamanho BIGINT,
    caminho_arquivo VARCHAR(500) NOT NULL,

    data_upload TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_anexo_paciente
        FOREIGN KEY (paciente_id)
        REFERENCES paciente(id)
);