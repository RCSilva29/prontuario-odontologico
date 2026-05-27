export interface Usuario {
  id: number;
  nome: string;
  email: string;
  perfil: 'ADMIN' | 'DENTISTA';
  ativo: boolean;

  bloqueado: boolean;
  tentativasLogin: number;
  trocaSenhaObrigatoria: boolean;

  dataCriacao: string;
}

export interface UsuarioRequest {
  nome: string;
  email: string;
  senha?: string;
  perfil: 'ADMIN' | 'DENTISTA';
  ativo?: boolean;
}