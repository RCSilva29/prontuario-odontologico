export interface LoginRequest {
  email: string;
  senha: string;
}

export interface LoginResponse {
  id: number;
  nome: string;
  email: string;
  perfil: 'ADMIN' | 'DENTISTA';
  mensagem: string;
}

export interface LoginResponse {
  id: number;
  nome: string;
  email: string;
  perfil: 'ADMIN' | 'DENTISTA';
  token: string;
  mensagem: string;
}