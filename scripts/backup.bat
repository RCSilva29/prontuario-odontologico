@echo off
cd /d %~dp0..
setlocal

set DATA=%date:~6,4%-%date:~3,2%-%date:~0,2%
set HORA=%time:~0,2%-%time:~3,2%-%time:~6,2%
set HORA=%HORA: =0%

set BACKUP_DIR=backups\backup-%DATA%-%HORA%

mkdir "%BACKUP_DIR%"

echo Gerando backup do banco PostgreSQL...

docker exec prontuario-postgres pg_dump -U prontuario_user -d prontuario_odonto > "%BACKUP_DIR%\banco.sql"

echo Copiando anexos...

if exist anexos (
    xcopy anexos "%BACKUP_DIR%\anexos\" /E /I /Y
)

echo.
echo Backup finalizado com sucesso:
echo %BACKUP_DIR%
echo.

pause