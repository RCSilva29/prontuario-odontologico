export interface Consulta {
  id: number;
  pacienteId: number;
  pacienteNome: string;
  dentistaId: number;
  dentistaNome: string;
  dataHoraInicio: string;
  dataHoraFim: string;
  observacao?: string;
  status: string;
}

export interface ConsultaRequest {
  pacienteId: number;
  dentistaId: number;
  dataHoraInicio: string;
  dataHoraFim: string;
  observacao?: string;
}