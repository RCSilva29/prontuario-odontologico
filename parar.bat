@echo off
title Prontuario Odontologico - Parar

echo ==========================================
echo  Parando Prontuario Odontologico
echo ==========================================
echo.

docker compose down

if errorlevel 1 (
    echo Erro ao parar o sistema.
    pause
    exit /b 1
)

echo.
echo Sistema parado com sucesso.
echo.

pause