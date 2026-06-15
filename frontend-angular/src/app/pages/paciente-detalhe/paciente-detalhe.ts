import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { Paciente } from '../../models/paciente.model';
import { PacienteService } from '../../services/paciente.service';
import { DocumentoService } from '../../services/documento.service';

import { AnamneseCard } from '../../components/anamnese-card/anamnese-card';
import { AtendimentosCard } from '../../components/atendimentos-card/atendimentos-card';
import { OdontogramaCard } from '../../components/odontograma-card/odontograma-card';
import { AnexosCard } from '../../components/anexos-card/anexos-card';

import {
  Orcamento,
  OrcamentoItem,
  OrcamentoPagamentoRequest,
  OrcamentoRequest
} from '../../models/orcamento.model';
import { OrcamentoService } from '../../services/orcamento.service';

@Component({
  selector: 'app-paciente-detalhe',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    FormsModule,
    AnamneseCard,
    AtendimentosCard,
    OdontogramaCard,
    AnexosCard
  ],
  templateUrl: './paciente-detalhe.html',
  styleUrl: './paciente-detalhe.scss'
})
export class PacienteDetalhe implements OnInit {

  paciente?: Paciente;
  carregando = false;
  erro = '';
  erroOrcamento = '';
  campoErroOrcamento = '';

  exibindoAtestado = false;
  textoAtestado = '';
  gerandoAtestado = false;

  exibindoReceituario = false;
  prescricaoReceituario = '';
  orientacoesReceituario = '';
  gerandoReceituario = false;

  exibindoProntuario = false;
  gerandoProntuario = false;

  prontuarioOpcoes = {
    incluirAnamnese: true,
    incluirOdontograma: true,
    incluirAtendimentos: true
  };

  orcamentos: Orcamento[] = [];
  carregandoOrcamentos = false;
  exibindoOrcamento = false;
  salvandoOrcamento = false;
  orcamentoEditando?: Orcamento;

  exibindoPagamento = false;
  salvandoPagamento = false;
  orcamentoPagamento?: Orcamento;

  pagamentoForm: OrcamentoPagamentoRequest = {
    dataPagamento: '',
    valorPago: 0,
    formaPagamento: 'PIX',
    observacao: ''
  };

  orcamentoForm: OrcamentoRequest = {
    validadeDias: 30,
    observacoes: '',
    desconto: 0,
    status: 'ABERTO',
    itens: []
  };

  gerandoOrcamentoPdf = false;

  constructor(
    private route: ActivatedRoute,
    private pacienteService: PacienteService,
    private documentoService: DocumentoService,
    private orcamentoService: OrcamentoService
  ) { }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.carregarPaciente(id);
    this.carregarOrcamentos(id);
  }

  carregarPaciente(id: number): void {
    this.carregando = true;
    this.erro = '';

    this.pacienteService.buscarPorId(id).subscribe({
      next: (dados) => {
        this.paciente = dados;
        this.carregando = false;
      },
      error: () => {
        this.erro = 'Erro ao carregar paciente';
        this.carregando = false;
      }
    });
  }

  abrirAtestado(): void {
    this.erro = '';
    this.exibindoAtestado = true;
    this.textoAtestado =
      'Atesto para os devidos fins que o paciente compareceu ao atendimento odontológico nesta data.';
  }

  cancelarAtestado(): void {
    this.exibindoAtestado = false;
    this.textoAtestado = '';
    this.gerandoAtestado = false;
  }

  gerarAtestado(): void {
    this.erro = '';

    if (!this.paciente) {
      this.erro = 'Paciente não identificado para gerar atestado';
      return;
    }

    if (!this.textoAtestado.trim()) {
      this.erro = 'Informe o texto do atestado';
      return;
    }

    this.gerandoAtestado = true;

    this.documentoService
      .gerarAtestado(this.paciente.id, this.textoAtestado.trim())
      .subscribe({
        next: (pdf) => {
          this.gerandoAtestado = false;

          this.salvarEAbrirPdf(
            pdf,
            this.nomeArquivoDocumento('atestado')
          );

          this.cancelarAtestado();
        },
        error: (erro) => {
          this.gerandoAtestado = false;
          this.erro =
            erro?.error?.erro ||
            erro?.error?.message ||
            'Erro ao gerar atestado';
        }
      });
  }

  abrirReceituario(): void {
    this.erro = '';
    this.exibindoReceituario = true;
    this.prescricaoReceituario =
      '1. Medicamento / concentração\nTomar conforme orientação profissional.\n';
    this.orientacoesReceituario = '';
  }

  cancelarReceituario(): void {
    this.exibindoReceituario = false;
    this.prescricaoReceituario = '';
    this.orientacoesReceituario = '';
    this.gerandoReceituario = false;
  }

  gerarReceituario(): void {
    this.erro = '';

    if (!this.paciente) {
      this.erro = 'Paciente não identificado para gerar receituário';
      return;
    }

    if (!this.prescricaoReceituario.trim()) {
      this.erro = 'Informe a prescrição do receituário';
      return;
    }

    this.gerandoReceituario = true;

    this.documentoService
      .gerarReceituario(
        this.paciente.id,
        this.prescricaoReceituario.trim(),
        ''
      )
      .subscribe({
        next: (pdf) => {
          this.gerandoReceituario = false;

          this.salvarEAbrirPdf(
            pdf,
            this.nomeArquivoDocumento('receituario')
          );

          this.cancelarReceituario();
        },
        error: (erro) => {
          this.gerandoReceituario = false;
          this.erro =
            erro?.error?.erro ||
            erro?.error?.message ||
            'Erro ao gerar receituário';
        }
      });
  }

  abrirProntuario(): void {
    this.erro = '';
    this.exibindoProntuario = true;

    this.prontuarioOpcoes = {
      incluirAnamnese: true,
      incluirOdontograma: true,
      incluirAtendimentos: true
    };
  }

  cancelarProntuario(): void {
    this.exibindoProntuario = false;
    this.gerandoProntuario = false;

    this.prontuarioOpcoes = {
      incluirAnamnese: true,
      incluirOdontograma: true,
      incluirAtendimentos: true
    };
  }

  gerarProntuario(): void {
    this.erro = '';

    if (!this.paciente) {
      this.erro = 'Paciente não identificado para gerar prontuário';
      return;
    }

    if (
      !this.prontuarioOpcoes.incluirAnamnese &&
      !this.prontuarioOpcoes.incluirOdontograma &&
      !this.prontuarioOpcoes.incluirAtendimentos
    ) {
      this.erro = 'Selecione ao menos uma opção para gerar o prontuário';
      return;
    }

    this.gerandoProntuario = true;

    this.documentoService
      .gerarProntuario(this.paciente.id, this.prontuarioOpcoes)
      .subscribe({
        next: (pdf) => {
          this.gerandoProntuario = false;

          this.salvarEAbrirPdf(
            pdf,
            this.nomeArquivoProntuario()
          );

          this.cancelarProntuario();
        },
        error: (erro) => {
          this.gerandoProntuario = false;
          this.erro =
            erro?.error?.erro ||
            erro?.error?.message ||
            'Erro ao gerar prontuário';
        }
      });
  }

  private salvarEAbrirPdf(pdf: Blob, nomeArquivo: string): void {
    const blob = new Blob([pdf], { type: 'application/pdf' });
    const url = window.URL.createObjectURL(blob);

    const link = document.createElement('a');
    link.href = url;
    link.download = nomeArquivo;

    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    setTimeout(() => {
      window.URL.revokeObjectURL(url);
    }, 3000);
  }

  private nomeArquivoDocumento(prefixo: 'atestado' | 'receituario' | 'prontuario'): string {
    const nomePaciente = (this.paciente?.nome || 'paciente')
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .replace(/[^a-zA-Z0-9]/g, '_')
      .replace(/_+/g, '_')
      .replace(/^_|_$/g, '')
      .toLowerCase();

    const hoje = new Date();

    const dia = String(hoje.getDate()).padStart(2, '0');
    const mes = String(hoje.getMonth() + 1).padStart(2, '0');
    const ano = hoje.getFullYear();

    return `${prefixo}_${nomePaciente}_${dia}_${mes}_${ano}.pdf`;
  }

  formatarCpf(cpf: string): string {
    const valor = (cpf || '').replace(/\D/g, '');

    if (valor.length !== 11) {
      return cpf || '';
    }

    return valor.replace(/^(\d{3})(\d{3})(\d{3})(\d{2})$/, '$1.$2.$3-$4');
  }

  formatarTelefone(telefone: string): string {
    const valor = (telefone || '').replace(/\D/g, '');

    if (valor.length === 11) {
      return valor.replace(/^(\d{2})(\d{5})(\d{4})$/, '($1) $2-$3');
    }

    if (valor.length === 10) {
      return valor.replace(/^(\d{2})(\d{4})(\d{4})$/, '($1) $2-$3');
    }

    return telefone || '';
  }

  formatarData(data: string): string {
    if (!data) {
      return '-';
    }

    const partes = data.split('-');

    if (partes.length !== 3) {
      return data;
    }

    return `${partes[2]}/${partes[1]}/${partes[0]}`;
  }

  private nomeArquivoProntuario(): string {
    const nomePaciente = (this.paciente?.nome || 'paciente')
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .replace(/[^a-zA-Z0-9]/g, '_')
      .replace(/_+/g, '_')
      .replace(/^_|_$/g, '')
      .toLowerCase();

    const hoje = new Date();

    const dia = String(hoje.getDate()).padStart(2, '0');
    const mes = String(hoje.getMonth() + 1).padStart(2, '0');
    const ano = hoje.getFullYear();

    let tipo = 'Completo';

    if (
      this.prontuarioOpcoes.incluirAnamnese &&
      !this.prontuarioOpcoes.incluirOdontograma &&
      !this.prontuarioOpcoes.incluirAtendimentos
    ) {
      tipo = 'Anamnese';
    }

    if (
      !this.prontuarioOpcoes.incluirAnamnese &&
      this.prontuarioOpcoes.incluirOdontograma &&
      !this.prontuarioOpcoes.incluirAtendimentos
    ) {
      tipo = 'Odontograma';
    }

    if (
      !this.prontuarioOpcoes.incluirAnamnese &&
      !this.prontuarioOpcoes.incluirOdontograma &&
      this.prontuarioOpcoes.incluirAtendimentos
    ) {
      tipo = 'Atendimento';
    }

    return `Prontuario_${tipo}_${nomePaciente}_${dia}_${mes}_${ano}.pdf`;
  }

  carregarOrcamentos(pacienteId: number): void {
    this.carregandoOrcamentos = true;

    this.orcamentoService.listarPorPaciente(pacienteId).subscribe({
      next: (dados) => {
        this.orcamentos = dados;
        this.carregandoOrcamentos = false;
      },
      error: () => {
        this.erro = 'Erro ao carregar orçamentos';
        this.carregandoOrcamentos = false;
      }
    });
  }

  abrirNovoOrcamento(): void {
    this.erro = '';
    this.erroOrcamento = '';
    this.orcamentoEditando = undefined;
    this.exibindoOrcamento = true;

    this.orcamentoForm = {
      validadeDias: 30,
      observacoes: '',
      desconto: 0,
      status: 'ABERTO',
      itens: [
        {
          tipoDente: 'DENTES_ESPECIFICOS',
          dentes: '',
          procedimento: '',
          quantidade: 1,
          valorUnitario: 0
        }
      ]
    };
  }

  editarOrcamento(orcamento: Orcamento): void {
    this.erro = '';
    this.erroOrcamento = '';
    this.campoErroOrcamento = '';
    this.orcamentoEditando = orcamento;
    this.exibindoOrcamento = true;

    this.orcamentoForm = {
      validadeDias: orcamento.validadeDias,
      observacoes: orcamento.observacoes || '',
      desconto: orcamento.desconto || 0,
      status: orcamento.status || 'ABERTO',
      itens: orcamento.itens.map(item => ({
        id: item.id,
        tipoDente: this.obterTipoDenteOrcamento(item.dentes || ''),
        dentes: item.dentes || '',
        procedimento: item.procedimento,
        quantidade: item.quantidade,
        valorUnitario: item.valorUnitario
      }))
    };
  }

  cancelarOrcamento(): void {
    this.erroOrcamento = '';
    this.campoErroOrcamento = '';
    this.exibindoOrcamento = false;
    this.salvandoOrcamento = false;
    this.orcamentoEditando = undefined;
  }

  adicionarItemOrcamento(): void {
    this.erroOrcamento = '';
    this.campoErroOrcamento = '';
    this.orcamentoForm.itens.push({
      tipoDente: 'DENTES_ESPECIFICOS',
      dentes: '',
      procedimento: '',
      quantidade: 1,
      valorUnitario: 0
    });
  }

  removerItemOrcamento(index: number): void {
    if (this.orcamentoForm.itens.length === 1) {
      this.erroOrcamento = 'O orçamento precisa ter ao menos um item';
      return;
    }

    this.orcamentoForm.itens.splice(index, 1);
  }

  salvarOrcamento(): void {
    this.erro = '';
    this.erroOrcamento = '';
    this.campoErroOrcamento = '';

    if (!this.paciente) {
      this.erroOrcamento = 'Paciente não identificado';
      return;
    }

    if (!this.orcamentoForm.itens.length) {
      this.erroOrcamento = 'Inclua ao menos um item no orçamento';
      return;
    }

    for (let i = 0; i < this.orcamentoForm.itens.length; i++) {
      const item = this.orcamentoForm.itens[i];

      if (!item.procedimento || !item.procedimento.trim()) {
        this.marcarCampoObrigatorioOrcamento(`procedimento${i}`);
        return;
      }

      if (!item.quantidade || item.quantidade <= 0) {
        this.marcarCampoObrigatorioOrcamento(`quantidade${i}`);
        return;
      }

      if (!item.valorUnitario || item.valorUnitario <= 0) {
        this.marcarCampoObrigatorioOrcamento(`valorUnitario${i}`);
        return;
      }
    }

    this.salvandoOrcamento = true;

    const request: OrcamentoRequest = {
      validadeDias: this.orcamentoForm.validadeDias || 30,
      observacoes: this.orcamentoForm.observacoes || '',
      desconto: this.orcamentoForm.desconto || 0,
      status: 'ABERTO',
      itens: this.orcamentoForm.itens.map(item => ({
        dentes: item.dentes || '',
        procedimento: item.procedimento.trim(),
        quantidade: Number(item.quantidade),
        valorUnitario: Number(item.valorUnitario)
      }))
    };

    const requisicao = this.orcamentoEditando
      ? this.orcamentoService.atualizar(this.orcamentoEditando.id, request)
      : this.orcamentoService.criar(this.paciente.id, request);

    requisicao.subscribe({
      next: () => {
        this.salvandoOrcamento = false;
        this.cancelarOrcamento();
        this.carregarOrcamentos(this.paciente!.id);
      },
      error: (erro) => {
        this.salvandoOrcamento = false;
        this.erroOrcamento =
          erro?.error?.erro ||
          erro?.error?.message ||
          'Erro ao salvar orçamento';
      }
    });
  }

  excluirOrcamento(orcamento: Orcamento): void {
    const confirmar = confirm(`Confirma excluir o orçamento #${orcamento.id}?`);

    if (!confirmar) {
      return;
    }

    this.orcamentoService.excluir(orcamento.id).subscribe({
      next: () => {
        if (this.paciente) {
          this.carregarOrcamentos(this.paciente.id);
        }
      },
      error: () => {
        this.erro = 'Erro ao excluir orçamento';
      }
    });
  }

  subtotalItem(item: OrcamentoItem): number {
    return Number(item.quantidade || 0) * Number(item.valorUnitario || 0);
  }

  subtotalOrcamentoAtual(): number {
    return this.orcamentoForm.itens
      .map(item => this.subtotalItem(item))
      .reduce((total, valor) => total + valor, 0);
  }

  totalOrcamentoAtual(): number {
    const total = this.subtotalOrcamentoAtual() - Number(this.orcamentoForm.desconto || 0);
    return total < 0 ? 0 : total;
  }

  formatarMoeda(valor: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(valor || 0);
  }

  formatarDataHora(data: string): string {
    if (!data) {
      return '-';
    }

    return new Date(data).toLocaleDateString('pt-BR');
  }

  aplicarTipoDenteOrcamento(item: OrcamentoItem): void {
    if (item.tipoDente === 'DENTES_ESPECIFICOS') {
      item.dentes = '';
      return;
    }

    item.dentes = item.tipoDente || '';
  }

  obterTipoDenteOrcamento(dentes: string): string {
    if (
      dentes === 'PROTESE_TOTAL_SUPERIOR' ||
      dentes === 'PROTESE_TOTAL_INFERIOR' ||
      dentes === 'PROTESE_TOTAL_COMPLETA'
    ) {
      return dentes;
    }

    return 'DENTES_ESPECIFICOS';
  }

  abrirPagamento(orcamento: Orcamento): void {
    this.erro = '';
    this.orcamentoPagamento = orcamento;
    this.exibindoPagamento = true;

    const hoje = new Date();
    const ano = hoje.getFullYear();
    const mes = String(hoje.getMonth() + 1).padStart(2, '0');
    const dia = String(hoje.getDate()).padStart(2, '0');

    this.pagamentoForm = {
      dataPagamento: `${ano}-${mes}-${dia}T12:00`,
      valorPago: orcamento.saldoDevedor || 0,
      formaPagamento: 'PIX',
      observacao: ''
    };
  }

  cancelarPagamento(): void {
    this.exibindoPagamento = false;
    this.salvandoPagamento = false;
    this.orcamentoPagamento = undefined;

    this.pagamentoForm = {
      dataPagamento: '',
      valorPago: 0,
      formaPagamento: 'PIX',
      observacao: ''
    };
  }

  registrarPagamento(): void {
    this.erro = '';

    if (!this.orcamentoPagamento) {
      this.erro = 'Orçamento não identificado para registrar pagamento';
      return;
    }

    if (!this.pagamentoForm.valorPago || this.pagamentoForm.valorPago <= 0) {
      this.erro = 'Informe um valor de pagamento maior que zero';
      return;
    }

    this.salvandoPagamento = true;

    const request = {
      ...this.pagamentoForm,
      dataPagamento: this.pagamentoForm.dataPagamento
        ? `${this.pagamentoForm.dataPagamento}:00`
        : undefined
    };

    this.orcamentoService
      .registrarPagamento(this.orcamentoPagamento.id, request)
      .subscribe({
        next: () => {
          this.salvandoPagamento = false;
          this.cancelarPagamento();

          if (this.paciente) {
            this.carregarOrcamentos(this.paciente.id);
          }
        },
        error: (erro) => {
          this.salvandoPagamento = false;
          this.erro =
            erro?.error?.erro ||
            erro?.error?.message ||
            'Erro ao registrar pagamento';
        }
      });
  }

  excluirPagamento(orcamento: Orcamento, pagamentoId?: number): void {
    if (!pagamentoId) {
      return;
    }

    const confirmar = confirm('Confirma excluir este pagamento?');

    if (!confirmar) {
      return;
    }

    this.orcamentoService.excluirPagamento(orcamento.id, pagamentoId).subscribe({
      next: () => {
        if (orcamento.pagamentos) {
          orcamento.pagamentos = orcamento.pagamentos.filter(
            pagamento => pagamento.id !== pagamentoId
          );
        }

        this.orcamentoService.buscarPorId(orcamento.id).subscribe({
          next: (orcamentoAtualizado) => {
            this.orcamentoEditando = orcamentoAtualizado;

            this.orcamentos = this.orcamentos.map(item =>
              item.id === orcamentoAtualizado.id ? orcamentoAtualizado : item
            );
          },
          error: () => {
            if (this.paciente) {
              this.carregarOrcamentos(this.paciente.id);
            }
          }
        });
      },
      error: () => {
        this.erro = 'Erro ao excluir pagamento';
      }
    });
  }

  formatarStatusOrcamento(status: string): string {
    if (status === 'PARCIALMENTE_PAGO') {
      return 'PARCIALMENTE PAGO';
    }

    return status || '-';
  }

  gerarOrcamentoPdf(orcamentoId: number): void {
    this.erro = '';
    this.gerandoOrcamentoPdf = true;

    this.documentoService.gerarOrcamento(orcamentoId).subscribe({
      next: (pdf: Blob) => {
        this.gerandoOrcamentoPdf = false;

        this.salvarEAbrirPdf(
          pdf,
          this.nomeArquivoOrcamento(orcamentoId)
        );
      },
      error: (erro: any) => {
        this.gerandoOrcamentoPdf = false;
        this.erro =
          erro?.error?.erro ||
          erro?.error?.message ||
          'Erro ao gerar PDF do orçamento';
      }
    });
  }

  private nomeArquivoOrcamento(orcamentoId: number): string {
    const nomePaciente = (this.paciente?.nome || 'paciente')
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .replace(/[^a-zA-Z0-9]/g, '_')
      .replace(/_+/g, '_')
      .replace(/^_|_$/g, '')
      .toLowerCase();

    const hoje = new Date();
    const dia = String(hoje.getDate()).padStart(2, '0');
    const mes = String(hoje.getMonth() + 1).padStart(2, '0');
    const ano = hoje.getFullYear();

    const orcamentosOrdenados = [...this.orcamentos].sort((a, b) => {
      const dataA = new Date(a.dataCriacao).getTime();
      const dataB = new Date(b.dataCriacao).getTime();

      if (dataA !== dataB) {
        return dataA - dataB;
      }

      return a.id - b.id;
    });

    const indice = orcamentosOrdenados.findIndex(orcamento => orcamento.id === orcamentoId);
    const numeroOrcamentoPaciente = indice >= 0 ? indice + 1 : orcamentoId;

    return `orcamento_${numeroOrcamentoPaciente}_${nomePaciente}_${dia}_${mes}_${ano}.pdf`;
  }

  abrirOrcamentoComDentes(dentes: string[]): void {
    this.erro = '';
    this.erroOrcamento = '';
    this.campoErroOrcamento = '';

    if (!dentes || dentes.length === 0) {
      this.erroOrcamento = 'Selecione ao menos um dente para adicionar ao orçamento';
      return;
    }

    const dentesTexto = dentes.join(',');

    const orcamentoAberto = this.orcamentos
      .filter(orcamento => orcamento.status === 'ABERTO')
      .sort((a, b) => {
        const dataA = new Date(a.dataCriacao).getTime();
        const dataB = new Date(b.dataCriacao).getTime();

        if (dataA !== dataB) {
          return dataB - dataA;
        }

        return b.id - a.id;
      })[0];

    if (orcamentoAberto) {
      this.editarOrcamento(orcamentoAberto);

      this.orcamentoForm.itens.push({
        tipoDente: 'DENTES_ESPECIFICOS',
        dentes: dentesTexto,
        procedimento: '',
        quantidade: 1,
        valorUnitario: 0
      });

      return;
    }

    this.orcamentoEditando = undefined;
    this.exibindoOrcamento = true;

    this.orcamentoForm = {
      validadeDias: 30,
      observacoes: '',
      desconto: 0,
      status: 'ABERTO',
      itens: [
        {
          tipoDente: 'DENTES_ESPECIFICOS',
          dentes: dentesTexto,
          procedimento: '',
          quantidade: 1,
          valorUnitario: 0
        }
      ]
    };
  }

  private marcarCampoObrigatorioOrcamento(nomeCampo: string): void {
    this.erroOrcamento = 'Campo obrigatório não preenchido';
    this.campoErroOrcamento = nomeCampo;

    setTimeout(() => {
      const campo = document.querySelector(`[name="${nomeCampo}"]`) as HTMLInputElement | null;

      if (campo) {
        campo.scrollIntoView({
          behavior: 'smooth',
          block: 'center'
        });

        campo.focus();
      }
    }, 100);
  }
}