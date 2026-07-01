export interface Usuario {
  id: number;
  nome: string;
  email: string;
  perfil: 'ADMIN' | 'DENTISTA';
  ativo: boolean;
  dataCriacao: string;
  bloqueado?: boolean;
  tentativasLogin?: number;
  trocaSenhaObrigatoria?: boolean;
  especialidade?: string;
  cro?: string;
  telefone?: string;
}

export interface UsuarioRequest {
  nome: string;
  email: string;
  senha?: string;
  perfil: 'ADMIN' | 'DENTISTA';
  ativo?: boolean;
  especialidade?: string;
  cro?: string;
  telefone?: string;
}