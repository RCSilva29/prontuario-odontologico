export interface Odontograma {
  id: number;
  pacienteId: number;
  numeroDente: string;
  status: string;
  observacao: string;
  dataRegistro: string;
}

export interface OdontogramaRequest {
  numeroDente: string;
  status: string;
  observacao: string;
}