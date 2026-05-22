export interface Atendimento {
  id: number;
  pacienteId: number;
  dataAtendimento: string;
  queixaPrincipal: string;
  evolucaoClinica: string;
  procedimentoRealizado: string;
  observacoes: string;
}

export interface AtendimentoRequest {
  queixaPrincipal: string;
  evolucaoClinica: string;
  procedimentoRealizado: string;
  observacoes: string;
}