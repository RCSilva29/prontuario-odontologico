@echo off
setlocal EnableExtensions
title Instalador - Prontuario Odontologico

cd /d "%~dp0"

echo.
echo ============================================================
echo      INSTALADOR - PRONTUARIO ODONTOLOGICO
echo ============================================================
echo.

if not exist "docker-compose.yml" (
    echo [ERRO] O arquivo docker-compose.yml nao foi encontrado.
    echo Coloque este instalador na pasta principal do sistema.
    pause
    exit /b 1
)

if not exist "backend-springboot" (
    echo [ERRO] A pasta backend-springboot nao foi encontrada.
    echo A pasta principal deve conter backend-springboot, frontend-angular e docker-compose.yml.
    pause
    exit /b 1
)

if not exist "frontend-angular" (
    echo [ERRO] A pasta frontend-angular nao foi encontrada.
    echo A pasta principal deve conter backend-springboot, frontend-angular e docker-compose.yml.
    pause
    exit /b 1
)

where docker >nul 2>nul
if errorlevel 1 (
    echo [ERRO] Docker Desktop nao foi encontrado.
    echo Instale o Docker Desktop, reinicie o computador e execute este instalador novamente.
    echo.
    echo https://www.docker.com/products/docker-desktop/
    pause
    exit /b 1
)

echo Verificando Docker Desktop...
docker info >nul 2>nul
if errorlevel 1 (
    echo.
    echo [ATENCAO] O Docker Desktop nao esta iniciado.
    echo Abra o Docker Desktop e aguarde aparecer "Engine running".
    echo Depois pressione qualquer tecla para continuar.
    pause >nul
    docker info >nul 2>nul
    if errorlevel 1 (
        echo.
        echo [ERRO] O Docker ainda nao esta pronto.
        echo Reinicie o computador, abra o Docker Desktop e tente novamente.
        pause
        exit /b 1
    )
)

if not exist "anexos" mkdir "anexos"
if not exist "backups" mkdir "backups"
if not exist "scripts" mkdir "scripts"

echo.
echo Criando atalhos na Area de Trabalho...
powershell -NoProfile -ExecutionPolicy Bypass -File "scripts\criar-atalhos.ps1"

echo.
echo Instalando e iniciando o sistema...
docker compose up -d --build
if errorlevel 1 (
    echo.
    echo [ERRO] Nao foi possivel iniciar o sistema.
    echo Verifique as mensagens acima.
    pause
    exit /b 1
)

echo.
echo Aguardando o sistema iniciar...
timeout /t 12 /nobreak >nul

echo.
echo Abrindo o Prontuario Odontologico...
start "" "http://localhost:4200"

echo.
echo ============================================================
echo Instalacao concluida.
echo.
echo Primeiro acesso:
echo Email: admin@odonto.com
echo Senha: 123456
echo.
echo Altere a senha apos entrar no sistema.
echo ============================================================
echo.
pause
exit /b 0
