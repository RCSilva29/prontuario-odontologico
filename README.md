# 🦷 Prontuário Odontológico

**Versão 1.0**

Sistema desktop/local para gerenciamento de consultórios odontológicos, desenvolvido para funcionamento em ambiente local, com foco em simplicidade, segurança e independência de serviços externos.

---

# 📌 Objetivo do Sistema

O Prontuário Odontológico foi desenvolvido para permitir o gerenciamento de:

* Usuários
* Pacientes
* Prontuários
* Atendimentos
* Anamneses
* Odontogramas
* Documentos e anexos

Principais características:

* Funcionamento local
* Não depende de internet após instalação
* Controle de acesso por usuários
* Backup e restauração de dados
* Segurança de autenticação

---

# 📊 Status do Projeto

✅ Em utilização local

✅ Controle de usuários

✅ Controle de pacientes

✅ Autenticação JWT

✅ Controle de permissões

✅ Backup e restauração

✅ Reset emergencial de administrador

🚧 Auditoria de ações

🚧 Exportação PDF

🚧 Dashboard administrativo

---

# 🚀 Tecnologias Utilizadas

## Frontend

* Angular
* TypeScript
* SCSS

## Backend

* Java 17
* Spring Boot
* Spring Security
* JWT Authentication
* BCrypt

## Banco de Dados

* PostgreSQL 16

## Infraestrutura

* Docker
* Docker Compose

---

# 📁 Estrutura do Projeto

```text
prontuario-odontologico/
│
├── backend-spring/
├── frontend-angular/
├── anexos/
├── backups/
├── scripts/
│
├── Prontuário Odontológico
├── Encerrar Prontuário
├── Fazer Backup
│
├── docker-compose.yml
├── README.md
└── INSTALACAO.md
```

---

# ✅ Funcionalidades Implementadas

## 👥 Controle de Usuários

Perfis disponíveis:

* ADMIN
* DENTISTA

Recursos:

* Cadastro de usuários
* Edição de usuários
* Inativação de usuários
* Reativação de usuários
* Desbloqueio de usuários
* Redefinição de senha
* Alteração de senha própria
* Proteção contra remoção do último administrador

---

## 👤 Gestão de Pacientes

* Cadastro de pacientes
* Alteração de pacientes
* Controle de dados pessoais
* Histórico clínico
* Observações clínicas

---

## 🔐 Segurança

O sistema possui:

* Autenticação JWT
* Senhas criptografadas com BCrypt
* Bloqueio automático após tentativas inválidas
* Controle de perfis de acesso
* Proteção contra auto desbloqueio
* Proteção contra auto redefinição de senha
* Reset emergencial de administrador

---

# 🚫 Fluxo de Bloqueio

Após 3 tentativas de login inválidas:

* O usuário é bloqueado
* O acesso é impedido
* Apenas outro administrador poderá realizar o desbloqueio

---

# 🔑 Usuário Administrador Inicial

```text
Email:
admin@odonto.com

Senha:
123456
```

Após o primeiro acesso recomenda-se alterar imediatamente a senha.

---

# 🛠 Instalação

Consulte o arquivo:

```text
INSTALACAO.md
```

para instruções completas de instalação.

---

# ▶️ Utilização Diária

## Abrir Sistema

Clique em:

```text
🦷 Prontuário Odontológico
```

---

## Encerrar Sessão

Utilize o botão:

```text
Sair
```

disponível dentro do sistema.

---

## Encerrar Completamente o Sistema

Quando desejar desligar os serviços do sistema:

```text
🛑 Encerrar Prontuário
```

---

# 💾 Backup do Sistema

Para gerar uma cópia de segurança:

```text
💾 Fazer Backup
```

Os arquivos serão armazenados na pasta:

```text
backups
```

---

# ♻️ Restauração do Sistema

Utilize:

```text
scripts\restore.bat
```

Exemplo:

```text
restore.bat "backups\backup-AAAA-MM-DD-HH-MM-SS"
```

---

# 🚨 Reset Emergencial do Administrador

Em caso de bloqueio de todos os administradores:

```text
scripts\reset-admin.bat
```

O procedimento:

* Desbloqueia o administrador principal
* Define senha temporária
* Obriga troca de senha no próximo acesso

---

# 💻 Desenvolvimento Local

## Backend

```bash
cd backend-spring
./mvnw spring-boot:run
```

## Frontend

```bash
cd frontend-angular
npm install
ng serve
```

---

# 🔒 Recursos de Segurança

* JWT Authentication
* BCrypt Password
* Controle de Perfis
* Bloqueio Automático
* Proteção contra Auto Gerenciamento Crítico
* Proteção contra Remoção do Último Administrador
* Persistência em PostgreSQL

---

# 📈 Melhorias Futuras

Planejadas:

* Auditoria de ações
* Histórico de alterações
* Exportação PDF
* Dashboard administrativo
* Notificações
* Assinatura digital
* Instalador Windows (.exe)
* Criptografia de anexos
* Logs administrativos

---

# 📌 Observações

Este sistema foi desenvolvido para utilização local em consultórios odontológicos.

Após instalado, não depende de internet para funcionamento.

---

# 📄 Licença

Projeto privado.

Uso interno e restrito.
