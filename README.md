# 🦷 Prontuário Odontológico

Sistema desktop/local para gerenciamento de consultório odontológico.

---

# 📌 Objetivo do sistema

O sistema foi criado para permitir o gerenciamento local de:

- usuários
- pacientes
- prontuários
- atendimentos
- anamneses
- odontogramas
- anexos/documentos

com foco em:

- simplicidade
- funcionamento offline/local
- segurança de acesso
- facilidade de backup/restauração
- independência de serviços externos

---

# 🚀 Tecnologias utilizadas

## Frontend

- Angular
- TypeScript
- SCSS

## Backend

- Java 17
- Spring Boot
- Spring Security
- JWT Authentication
- BCrypt

## Banco de dados

- PostgreSQL 16

## Infraestrutura

- Docker
- Docker Compose

---

# 📁 Estrutura do projeto

```text
prontuario-odontologico/
│
├── backend-spring/
├── frontend-angular/
├── anexos/
├── backups/
├── scripts/
│   ├── instalar.bat
│   ├── iniciar.bat
│   ├── parar.bat
│   ├── backup.bat
│   ├── restore.bat
│   └── reset-admin.bat
│
├── docker-compose.yml
├── README.md
└── INSTALACAO.md
```

---

# ✅ Funcionalidades implementadas

## 👥 Controle de usuários

Perfis disponíveis:

- ADMIN
- DENTISTA

Recursos:

- cadastro de usuários
- edição de usuários
- inativação de usuários
- reativação de usuários
- redefinição de senha
- desbloqueio de usuário
- alteração de senha própria
- proteção contra remoção do último ADMIN

---

## 👤 Gestão de pacientes

- cadastro de pacientes
- edição de pacientes
- controle de dados pessoais
- observações clínicas

---

## 🔐 Segurança de login

O sistema possui:

- autenticação JWT
- senha criptografada com BCrypt
- bloqueio após 3 tentativas inválidas
- proteção contra auto desbloqueio
- proteção contra auto redefinição de senha
- proteção contra remoção do último ADMIN
- reset emergencial do administrador

---

# 🚫 Fluxo de bloqueio

Após 3 tentativas inválidas:

- o usuário é bloqueado
- login é impedido
- apenas outro ADMIN pode desbloquear

---

# 🛠 Reset emergencial do ADMIN

Arquivo:

```text
scripts/reset-admin.bat
```

Função:

- desbloquear admin principal
- redefinir senha temporária
- obrigar troca de senha

Senha temporária padrão:

```text
123456
```

---

# 💾 Backup do sistema

Arquivo:

```text
scripts/backup.bat
```

O backup inclui:

- banco PostgreSQL
- anexos/documentos

Arquivos gerados em:

```text
/backups
```

Formato:

```text
backup-AAAA-MM-DD-HH-MM-SS/
```

---

# ♻️ Restauração do sistema

Arquivo:

```text
scripts/restore.bat
```

Uso:

```bash
restore.bat "backups\NOME_DO_BACKUP"
```

A restauração:

- recria schema do banco
- restaura dados
- restaura anexos

---

# 🐳 Execução com Docker

## Subir containers

```bash
docker compose up -d
```

## Rebuild completo

```bash
docker compose up --build
```

## Derrubar containers

```bash
docker compose down
```

---

# 🌐 Portas utilizadas

| Serviço | Porta |
|---|---|
| Frontend Angular | 4200 |
| Backend Spring | 8080 |
| PostgreSQL | 5432 |

---

# 🔑 Acesso ao sistema

Frontend:

```text
http://localhost:4200
```

Backend:

```text
http://localhost:8080
```

---

# 👤 Usuário administrador inicial

```text
Email:
admin@odonto.com

Senha:
123456
```

No primeiro acesso recomenda-se alterar imediatamente a senha do administrador.

---

# ⚙️ Requisitos do sistema

Para funcionamento correto do Prontuário Odontológico, a máquina deve possuir:

- Windows 10 ou Windows 11
- Virtualização habilitada na BIOS
- Mínimo de 4 GB de memória RAM
- Docker Desktop instalado
- Internet apenas para instalação inicial do Docker

---

# 🐳 Instalação do Docker Desktop

## 1. Baixe o Docker Desktop

https://www.docker.com/products/docker-desktop/

---

## 2. Instale normalmente

Utilize as opções padrão do instalador.

---

## 3. Reinicie o computador

Após a instalação, reinicie o Windows.

---

## 4. Abra o Docker Desktop

Aguarde até aparecer:

```text
Engine running
```

---

# 🛠 Instalação do sistema

Após instalar o Docker Desktop:

1. Extraia a pasta do sistema.
2. Abra a pasta do projeto.
3. Execute:

```text
scripts\instalar.bat
```

O sistema irá:

- criar os containers
- configurar banco PostgreSQL
- iniciar backend e frontend
- abrir automaticamente o sistema no navegador

---

# ▶️ Uso diário

## Iniciar sistema

```text
scripts\iniciar.bat
```

---

## Parar sistema

```text
scripts\parar.bat
```

---

# 💻 Desenvolvimento local

## Backend

```bash
cd backend-spring
./mvnw spring-boot:run
```

---

## Frontend

```bash
cd frontend-angular
npm install
ng serve
```

---

# 🔒 Segurança implementada

- JWT Authentication
- BCrypt Password
- Controle de perfis
- Bloqueio automático
- Proteção contra remoção do último ADMIN
- Proteção contra auto gerenciamento crítico
- Reset emergencial offline
- Persistência em banco PostgreSQL

---

# 📈 Melhorias futuras

Planejadas:

- auditoria de ações
- histórico de alterações
- exportação PDF
- dashboard administrativo
- notificações
- assinatura digital
- instalação desktop (.exe)
- criptografia de anexos
- logs administrativos

---

# 📌 Observações

Este sistema foi desenvolvido para utilização local/privada em consultórios odontológicos.

Não depende de internet para funcionamento após instalação.

---

# 📄 Licença

Projeto privado. Uso interno/restrito.