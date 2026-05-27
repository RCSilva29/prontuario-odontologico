@echo off
title Prontuario Odontologico - Iniciar

echo ==========================================
echo  Iniciando Prontuario Odontologico
echo ==========================================
echo.

echo Verificando Docker...
docker --version >nul 2>&1

if errorlevel 1 (
    echo Docker nao encontrado ou nao esta iniciado.
    echo Abra o Docker Desktop e tente novamente.
    pause
    exit /b 1
)

echo Iniciando containers...
docker compose up -d

if errorlevel 1 (
    echo Erro ao iniciar o sistema.
    pause
    exit /b 1
)

echo.
echo Sistema iniciado com sucesso.
echo Abrindo no navegador...
echo.

start http://localhost:4200/login

pause