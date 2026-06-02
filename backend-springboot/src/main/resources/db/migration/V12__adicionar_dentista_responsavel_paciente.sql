ALTER TABLE paciente
ADD COLUMN dentista_id BIGINT;

ALTER TABLE paciente
ADD CONSTRAINT fk_paciente_dentista
FOREIGN KEY (dentista_id)
REFERENCES usuario(id);