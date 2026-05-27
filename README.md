Prontuário Odontológico

Sistema desktop/local para gerenciamento de consultório odontológico, desenvolvido com:

Frontend Angular
Backend Spring Boot
PostgreSQL
Docker / Docker Compose
Objetivo do sistema

O sistema foi criado para permitir o gerenciamento local de:

usuários
pacientes
prontuários
atendimentos
anamneses
odontogramas
anexos/documentos

com foco em:

simplicidade
funcionamento offline/local
segurança básica de acesso
facilidade de backup/restauração
Tecnologias utilizadas
Frontend
Angular
TypeScript
SCSS
Backend
Java 17
Spring Boot
Spring Security
JWT Authentication
BCrypt
Banco de dados
PostgreSQL 16
Infraestrutura
Docker
Docker Compose
Estrutura do projeto
prontuario-odontologico/
│
├── backend-spring/
├── frontend-angular/
├── backups/
├── anexos/
├── docker-compose.yml
├── backup.bat
├── restore.bat
├── reset-admin.bat
└── README.md
Funcionalidades implementadas
Controle de usuários

Perfis disponíveis:

ADMIN
DENTISTA

Recursos:

cadastro de usuários
edição
inativação
reativação
redefinição de senha
desbloqueio de usuário
alteração de senha própria
Segurança de login

O sistema possui:

autenticação JWT
senha criptografada com BCrypt
bloqueio após 3 tentativas inválidas
proteção contra auto desbloqueio
proteção contra auto redefinição de senha
proteção contra remoção do último ADMIN
reset emergencial do administrador
Fluxo de bloqueio

Após 3 tentativas inválidas:

o usuário é bloqueado
login é impedido
apenas outro ADMIN pode desbloquear
Reset emergencial do ADMIN

Arquivo:

reset-admin.bat

Função:

desbloquear admin principal
redefinir senha temporária
obrigar troca de senha

Senha temporária padrão:

123456
Backup do sistema

Arquivo:

backup.bat

O backup inclui:

banco PostgreSQL
anexos/documentos

Arquivos gerados em:

/backups

Formato:

backup-AAAA-MM-DD-HH-MM-SS/
Restauração do sistema

Arquivo:

restore.bat

Uso:

restore.bat "backups\NOME_DO_BACKUP"

A restauração:

recria schema do banco
restaura dados
restaura anexos
Execução com Docker
Subir containers
docker compose up -d
Rebuild completo
docker compose up --build
Derrubar containers
docker compose down
Portas utilizadas
Serviço	Porta
Frontend Angular	4200
Backend Spring	8080
PostgreSQL	5432
Acesso ao sistema

Frontend:

http://localhost:4200

Backend:

http://localhost:8080
Usuário administrador inicial
Email:
admin@odonto.com

Senha:
123456

O sistema pode exigir troca obrigatória de senha.

Requisitos

Necessário possuir instalado:

Docker Desktop
Git
Java 17 (opcional para desenvolvimento)
Node.js (opcional para desenvolvimento)
Desenvolvimento local
Backend
cd backend-spring
./mvnw spring-boot:run
Frontend
cd frontend-angular
npm install
ng serve
Segurança implementada
JWT Authentication
BCrypt Password
Controle de perfis
Bloqueio automático
Proteção contra remoção do último ADMIN
Proteção contra auto gerenciamento crítico
Melhorias futuras

Planejadas:

auditoria de ações
histórico de alterações
exportação PDF
dashboard
notificações
assinatura digital
instalação desktop
criptografia de anexos
logs administrativos
Observações

Este sistema foi desenvolvido para utilização local/privada em consultórios odontológicos.

Não depende de internet para funcionamento após instalação.

Licença

Projeto privado. Uso interno/restrito.