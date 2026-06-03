@echo off
setlocal EnableExtensions
cd /d "%~dp0\.."

where docker >nul 2>nul
if errorlevel 1 (
    msg * "Docker Desktop nao encontrado. Backup nao realizado."
    exit /b 1
)

docker info >nul 2>nul
if errorlevel 1 (
    msg * "Abra o Docker Desktop antes de fazer backup."
    exit /b 1
)

if not exist "backups" mkdir "backups"

for /f %%i in ('powershell -NoProfile -Command "Get-Date -Format yyyy-MM-dd_HH-mm-ss"') do set DATA=%%i
set DESTINO=backups\backup-%DATA%

mkdir "%DESTINO%"
mkdir "%DESTINO%\banco"
mkdir "%DESTINO%\anexos"

echo Gerando backup do banco...
docker exec prontuario-postgres pg_dump -U prontuario_user -d prontuario_odonto -F c -f /tmp/prontuario_odonto.backup
docker cp prontuario-postgres:/tmp/prontuario_odonto.backup "%DESTINO%\banco\prontuario_odonto.backup"

echo Copiando anexos...
if exist "anexos" xcopy "anexos" "%DESTINO%\anexos" /E /I /Y >nul

echo Compactando backup...
powershell -NoProfile -ExecutionPolicy Bypass -Command "Compress-Archive -Path '%DESTINO%' -DestinationPath '%DESTINO%.zip' -Force"

msg * "Backup concluido: %DESTINO%.zip"
exit /b 0
