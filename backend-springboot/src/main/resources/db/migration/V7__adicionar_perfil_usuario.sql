ALTER TABLE usuario
ADD COLUMN perfil VARCHAR(20) NOT NULL DEFAULT 'DENTISTA';

UPDATE usuario
SET perfil = 'ADMIN'
WHERE email = 'admin@odonto.com';