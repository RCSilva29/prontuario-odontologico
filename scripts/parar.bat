@echo off
setlocal
cd /d "%~dp0\.."

docker compose stop
msg * "Prontuario Odontologico encerrado."
exit /b 0
