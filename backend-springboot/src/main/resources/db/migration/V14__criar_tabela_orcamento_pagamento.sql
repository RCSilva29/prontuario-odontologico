CREATE TABLE orcamento_pagamento (
    id BIGSERIAL PRIMARY KEY,
    orcamento_id BIGINT NOT NULL,
    data_pagamento TIMESTAMP NOT NULL,
    valor_pago NUMERIC(12,2) NOT NULL,
    forma_pagamento VARCHAR(50),
    observacao TEXT,

    CONSTRAINT fk_orcamento_pagamento_orcamento
        FOREIGN KEY (orcamento_id)
        REFERENCES orcamento(id)
        ON DELETE CASCADE
);