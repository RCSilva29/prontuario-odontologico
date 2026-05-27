# 🛠 Instalação do Prontuário Odontológico

Este documento descreve o processo completo de instalação do sistema Prontuário Odontológico.

---

# 📋 Requisitos mínimos

A máquina deve possuir:

- Windows 10 ou Windows 11
- Virtualização habilitada na BIOS
- Mínimo de 4 GB de memória RAM
- Docker Desktop instalado

---

# 🐳 Instalação do Docker Desktop

## 1. Baixar Docker Desktop

Acesse:

https://www.docker.com/products/docker-desktop/

---

## 2. Executar instalação

Instale utilizando as opções padrão do instalador.

---

## 3. Reiniciar computador

Após finalizar a instalação, reinicie o Windows.

---

## 4. Abrir Docker Desktop

Abra o Docker Desktop e aguarde até aparecer:

```text
Engine running
```

O Docker precisa permanecer aberto durante o uso do sistema.

---

# 📦 Instalação do sistema

## 1. Extrair arquivos

Extraia a pasta do sistema em qualquer local do computador.

Exemplo:

```text
C:\ProntuarioOdontologico
```

---

## 2. Abrir pasta scripts

Abra:

```text
scripts
```

---

## 3. Executar instalação inicial

Execute:

```text
instalar.bat
```

O sistema irá automaticamente:

- criar containers Docker
- configurar banco PostgreSQL
- iniciar backend
- iniciar frontend
- abrir sistema no navegador

---

# 🔑 Primeiro acesso

Após instalação:

Acesse:

```text
http://localhost:4200
```

Usuário inicial:

```text
Email:
admin@odonto.com

Senha:
123456
```

Recomenda-se alterar imediatamente a senha do administrador.

---

# ▶️ Uso diário

## Iniciar sistema

Execute:

```text
scripts\iniciar.bat
```

---

## ⏹ Encerrar sistema

Execute:

```text
scripts\parar.bat
```

---

# 💾 Backup do sistema

Para gerar backup:

```text
scripts\backup.bat
```

Os arquivos serão salvos na pasta:

```text
backups
```

---

# ♻️ Restaurar backup

Para restaurar:

```text
scripts\restore.bat
```

Exemplo:

```text
restore.bat "backups\backup-2026-05-26-15-56-47"
```

---

# 🚨 Reset emergencial do administrador

Caso todos os administradores sejam bloqueados:

Execute:

```text
scripts\reset-admin.bat
```

Esse procedimento:

- desbloqueia administrador principal
- redefine senha temporária
- obriga troca de senha

---

# ⚠️ Problemas comuns

## Docker não encontrado

Verifique se o Docker Desktop está instalado.

---

## Docker não iniciado

Abra manualmente o Docker Desktop.

---

## Virtualização desabilitada

Ative a virtualização na BIOS da máquina.

---

# 📌 Observações importantes

- O sistema funciona localmente/offline.
- Não depende de internet após instalação.
- O Docker Desktop deve permanecer aberto durante o uso.
- Não remover manualmente os containers Docker.
- Recomenda-se backup periódico.

---

# 📞 Suporte

Em caso de problemas:

- verificar se o Docker Desktop está aberto
- reiniciar o computador
- executar novamente `iniciar.bat`