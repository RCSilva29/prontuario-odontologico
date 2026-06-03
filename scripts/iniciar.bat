@echo off
setlocal
cd /d "%~dp0\.."

where docker >nul 2>nul
if errorlevel 1 (
    msg * "Docker Desktop nao encontrado. Instale ou abra o Docker Desktop."
    exit /b 1
)

docker info >nul 2>nul
if errorlevel 1 (
    msg * "Abra o Docker Desktop e aguarde iniciar antes de abrir o Prontuario."
    exit /b 1
)

docker compose up -d
timeout /t 8 /nobreak >nul
start "" "http://localhost:4200"
exit /b 0
