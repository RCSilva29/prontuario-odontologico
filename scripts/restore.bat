@echo off
setlocal
cd /d "%~dp0\.."

if "%~1"=="" (
    echo Informe o caminho da pasta ou ZIP do backup.
    echo Exemplo:
    echo scripts\restore.bat "backups\backup-2026-06-03_10-00-00"
    pause
    exit /b 1
)

set BACKUP=%~1
set TEMP_RESTORE=

if /I "%BACKUP:~-4%"==".zip" (
    set TEMP_RESTORE=%TEMP%\restore-prontuario-%RANDOM%
    mkdir "%TEMP_RESTORE%"
    powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -Path '%BACKUP%' -DestinationPath '%TEMP_RESTORE%' -Force"
    for /d %%D in ("%TEMP_RESTORE%\backup-*") do set BACKUP=%%D
)

if not exist "%BACKUP%\banco\prontuario_odonto.backup" (
    echo Backup do banco nao encontrado em:
    echo %BACKUP%\banco\prontuario_odonto.backup
    pause
    exit /b 1
)

echo ATENCAO: a restauracao substituira os dados atuais.
pause

docker compose up -d postgres
timeout /t 8 /nobreak >nul

docker cp "%BACKUP%\banco\prontuario_odonto.backup" prontuario-postgres:/tmp/prontuario_odonto.backup
docker exec prontuario-postgres pg_restore -U prontuario_user -d prontuario_odonto --clean --if-exists /tmp/prontuario_odonto.backup

if exist "%BACKUP%\anexos" (
    if exist "anexos" rmdir /S /Q "anexos"
    xcopy "%BACKUP%\anexos" "anexos" /E /I /Y >nul
)

docker compose up -d
echo Restauracao concluida.
pause
exit /b 0
