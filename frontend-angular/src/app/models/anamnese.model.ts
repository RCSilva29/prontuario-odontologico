export interface Anamnese {
  id: number;
  hipertensao: boolean;
  diabetes: boolean;
  alergias: string;
  medicamentos: string;
  fumante: boolean;
  gravida: boolean;
  observacoes: string;
}

export interface AnamneseRequest {
  hipertensao: boolean;
  diabetes: boolean;
  alergias: string;
  medicamentos: string;
  fumante: boolean;
  gravida: boolean;
  observacoes: string;
}