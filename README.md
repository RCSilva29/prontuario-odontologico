# 🦷 Prontuário Odontológico

Sistema completo para gerenciamento de consultórios odontológicos, desenvolvido com **Spring Boot**, **Angular** e **PostgreSQL**, com implantação simplificada via **Docker Compose**.

O sistema foi projetado para operar inicialmente em ambiente local (consultório único), permitindo controle de pacientes, prontuários, odontogramas, orçamentos, pagamentos e agenda compartilhada entre dentistas.

---

# 📋 Funcionalidades

## 👥 Gestão de Pacientes

- Cadastro de pacientes
- Edição de pacientes
- Pesquisa de pacientes
- Exclusão lógica
- Histórico completo de atendimento

---

## 🩺 Anamnese

Registro das principais informações clínicas:

- Hipertensão
- Diabetes
- Alergias
- Medicamentos em uso
- Tabagismo
- Gravidez
- Observações clínicas

---

## 📎 Anexos

Gerenciamento de documentos do paciente:

- Radiografias
- Fotografias
- Exames
- Receitas
- Atestados
- Outros documentos

Upload e download diretamente pelo sistema.

---

## 🦷 Odontograma

Controle visual dos tratamentos odontológicos.

### Recursos

- Seleção individual de dentes
- Seleção múltipla de dentes
- Prótese total superior
- Prótese total inferior
- Prótese total completa

---

## 💰 Orçamentos

Controle completo de tratamentos e valores.

### Recursos

- Múltiplos procedimentos por orçamento
- Controle por paciente
- Numeração automática individual por paciente
- Integração com odontograma
- Cálculo automático de subtotais
- Descontos
- Validade do orçamento

---

## 💳 Pagamentos

Controle financeiro integrado.

### Recursos

- Registro de pagamentos
- Histórico de recebimentos
- Exclusão de pagamentos
- Saldo devedor automático

### Status automáticos

- ABERTO
- PARCIAL
- QUITADO

---

## 📄 Geração de PDF

### Orçamento

Geração profissional contendo:

- Dados do paciente
- Procedimentos
- Valores
- Pagamentos realizados
- Saldo devedor
- Telefones do consultório

---

## 📅 Agenda Compartilhada

Agenda visual compartilhada entre os profissionais.

### Recursos

- Visualização semanal
- Horários de 30 em 30 minutos
- Funcionamento entre 08:00 e 21:00
- Consulta rápida da ocupação do consultório

### Regras

- Não permite conflitos de horário
- Controle centralizado da agenda

### Reagendamento

Suporte a:

- Drag and Drop (arrastar e soltar)
- Mudança rápida de dia e horário

---

## 🧑‍⚕️ Controle por Dentista

Cada paciente possui um dentista responsável.

### Regras implementadas

- Pacientes só podem ser agendados com seu dentista responsável
- Administradores não podem ignorar essa regra
- Controle de integridade dos agendamentos

---

## 🎨 Identificação Visual

Consultas exibidas com cores distintas por dentista.

Facilita:

- Visualização rápida da agenda
- Organização dos atendimentos
- Controle de ocupação

---

## 📑 PDF da Agenda

Exportação da agenda em PDF.

### Agenda Semanal

Exibe:

- Período da semana
- Consultas organizadas por dia
- Paciente
- Dentista
- Observações

### Agenda Mensal

Exibe:

- Todo o mês selecionado
- Agrupamento por dia
- Visão consolidada dos atendimentos

---

# 🔐 Controle de Usuários

Perfis disponíveis:

## ADMIN

- Gerenciamento completo
- Cadastro de usuários
- Visualização global

## DENTISTA

- Acesso aos pacientes vinculados
- Controle de agenda
- Controle de tratamentos

---

# 🛠️ Tecnologias Utilizadas

## Backend

- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate
- Flyway

## Frontend

- Angular
- TypeScript
- SCSS

## Banco de Dados

- PostgreSQL

## Infraestrutura

- Docker
- Docker Compose

---

# 🚀 Instalação

## Pré-requisitos

- Docker Desktop
- Docker Compose

## Executar o sistema

```bash
docker compose up --build -d
```

## Acessar

Frontend:

```text
http://localhost:4200
```

Backend:

```text
http://localhost:8080
```

---

# 💾 Backup

O sistema possui suporte para:

- Backup do banco PostgreSQL
- Migração entre computadores
- Recuperação dos dados do consultório

---

# 📈 Roadmap

Funcionalidades planejadas:

- Dashboard inicial
- Indicadores financeiros
- Relatórios gerenciais
- Controle avançado de agenda
- Confirmação de consultas
- Integração com WhatsApp
- Histórico financeiro por paciente

---

# 📄 Licença

Projeto desenvolvido para uso em consultórios odontológicos privados.

Todos os direitos reservados.

---

# 👨‍💻 Autor

**Rafael Silva Carvalho**

Sistema desenvolvido para gestão clínica odontológica, com foco em simplicidade, produtividade e operação local segura.