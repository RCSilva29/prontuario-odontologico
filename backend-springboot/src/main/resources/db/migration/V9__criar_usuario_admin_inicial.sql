INSERT INTO usuario (
    nome,
    email,
    senha,
    perfil,
    ativo,
    data_criacao,
    tentativas_login,
    troca_senha_obrigatoria
)
SELECT
    'Administrador',
    'admin@odonto.com',
    '$2a$10$JeWYYhEf9SPKmsID5foSfX.3IPQwZNhJ3JPi4J/T9o6bPIHFkF3IQm',
    'ADMIN',
    TRUE,
    NOW(),
    0,
    FALSE
WHERE NOT EXISTS (
    SELECT 1
    FROM usuario
    WHERE email = 'admin@odonto.com'
);