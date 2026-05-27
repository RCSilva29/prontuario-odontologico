@echo off
cd /d %~dp0..
title Prontuario Odontologico - Reset Admin

echo ==========================================
echo  Reset emergencial do usuario ADMIN
echo ==========================================
echo.

echo Este procedimento ira:
echo - desbloquear o admin@odonto.com
echo - redefinir a senha temporaria para 123456
echo - obrigar troca de senha no proximo login
echo.

set /p CONFIRMA=Deseja continuar? Digite SIM para confirmar: 

if /I not "%CONFIRMA%"=="SIM" (
    echo Operacao cancelada.
    pause
    exit /b 0
)

echo.
echo Aplicando reset...

docker exec -i prontuario-postgres psql -U prontuario_user -d prontuario_odonto -c "UPDATE usuario SET senha = '$2a$10$SnIV/.TLLBFyCqNIDdHNbOCU7sc5tPMq57nNKY5rIJNkZ9OqZlJqi', bloqueado = false, tentativas_login = 0, troca_senha_obrigatoria = true, ativo = true WHERE email = 'admin@odonto.com';"

if errorlevel 1 (
    echo Erro ao resetar admin.
    pause
    exit /b 1
)

echo.
echo Reset concluido.
echo Login: admin@odonto.com
echo Senha temporaria: 123456
echo O sistema devera exigir troca de senha no proximo login.
echo.

pause