@echo off
title Prontuario Odontologico - Reiniciar

echo ==========================================
echo  Reiniciando Prontuario Odontologico
echo ==========================================
echo.

docker compose down

if errorlevel 1 (
    echo Erro ao parar o sistema.
    pause
    exit /b 1
)

docker compose up -d

if errorlevel 1 (
    echo Erro ao iniciar o sistema.
    pause
    exit /b 1
)

echo.
echo Sistema reiniciado com sucesso.
echo Abrindo no navegador...
echo.

start http://localhost:4200/login

pause