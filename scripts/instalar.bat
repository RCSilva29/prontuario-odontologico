@echo off
cd /d %~dp0..
title Prontuario Odontologico - Instalacao

echo ==========================================
echo  Instalando Prontuario Odontologico
echo ==========================================
echo.

echo Verificando Docker...
docker --version >nul 2>&1

if errorlevel 1 (
    echo Docker nao encontrado.
    echo Instale o Docker Desktop antes de continuar.
    pause
    exit /b 1
)

echo.
echo Verificando Docker Desktop...

docker info >nul 2>&1

if errorlevel 1 (
    echo Docker Desktop nao esta iniciado.
    echo Abra o Docker Desktop e tente novamente.
    pause
    exit /b 1
)

echo.
echo Criando containers...
docker compose up --build -d

if errorlevel 1 (
    echo Erro durante instalacao.
    pause
    exit /b 1
)

echo.
echo Instalacao concluida com sucesso.
echo Abrindo sistema...
echo.

timeout /t 5 >nul

start http://localhost:4200/login

pause