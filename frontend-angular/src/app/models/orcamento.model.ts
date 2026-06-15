export interface OrcamentoPagamento {
    id?: number;
    dataPagamento?: string;
    valorPago: number;
    formaPagamento?: string;
    observacao?: string;
}

export interface OrcamentoPagamentoRequest {
    dataPagamento?: string;
    valorPago: number;
    formaPagamento?: string;
    observacao?: string;
}

export interface OrcamentoItem {
    id?: number;
    tipoDente?: string;
    dentes?: string;
    procedimento: string;
    quantidade: number;
    valorUnitario: number;
    subtotal?: number;
}

export interface Orcamento {
    id: number;
    pacienteId: number;
    pacienteNome: string;
    usuarioId: number;
    usuarioNome: string;
    dataCriacao: string;
    validadeDias: number;
    observacoes?: string;
    subtotal: number;
    desconto: number;
    total: number;
    totalPago: number;
    saldoDevedor: number;
    status: string;
    itens: OrcamentoItem[];
    pagamentos: OrcamentoPagamento[];
}

export interface OrcamentoRequest {
    validadeDias: number;
    observacoes?: string;
    desconto: number;
    status: string;
    itens: OrcamentoItem[];
}