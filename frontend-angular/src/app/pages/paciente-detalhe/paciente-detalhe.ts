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

  constructor(
    private route: ActivatedRoute,
    private pacienteService: PacienteService,
    private documentoService: DocumentoService
  ) { }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.carregarPaciente(id);
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
}