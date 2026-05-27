@echo off
cd /d %~dp0..
setlocal

set "BACKUP_DIR=%~1"

if "%BACKUP_DIR%"=="" (
    echo Informe a pasta do backup.
    echo Exemplo:
    echo restore.bat "backups\backup-2026-05-26-15-20-10"
    pause
    exit /b 1
)

if not exist "%BACKUP_DIR%" (
    echo Pasta de backup nao encontrada:
    echo %BACKUP_DIR%
    pause
    exit /b 1
)

if not exist "%BACKUP_DIR%\banco.sql" (
    echo banco.sql nao encontrado.
    pause
    exit /b 1
)

echo Restaurando banco PostgreSQL...

docker exec -i prontuario-postgres psql -U prontuario_user -d prontuario_odonto -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"

type "%BACKUP_DIR%\banco.sql" | docker exec -i prontuario-postgres psql -U prontuario_user -d prontuario_odonto

echo Restaurando anexos...

if exist anexos rmdir /S /Q anexos
mkdir anexos

if exist "%BACKUP_DIR%\anexos" (
    xcopy "%BACKUP_DIR%\anexos" "anexos\" /E /I /Y
)

echo.
echo Restauracao finalizada com sucesso.
echo.

pause