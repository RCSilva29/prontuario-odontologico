import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { PacienteService } from '../../services/paciente.service';
import { Paciente } from '../../models/paciente.model';
import { DocumentoService } from '../../services/documento.service';

@Component({
  selector: 'app-pacientes',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './pacientes.html',
  styleUrl: './pacientes.scss'
})
export class Pacientes implements OnInit {

  pacientes: Paciente[] = [];
  carregando = false;
  erro = '';
  sucesso = '';

  pacienteAtestadoId?: number;
  textoAtestado = '';
  gerandoAtestado = false;
  exibindoAtestado = false;

  constructor(
    private pacienteService: PacienteService,
    private documentoService: DocumentoService
  ) { }

  ngOnInit(): void {
    this.carregarPacientes();
  }

  carregarPacientes(): void {
    this.carregando = true;
    this.erro = '';

    this.pacienteService.listar().subscribe({
      next: (dados) => {
        this.pacientes = dados;
        this.carregando = false;
      },
      error: () => {
        this.erro = 'Erro ao carregar pacientes';
        this.carregando = false;
      }
    });
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

  excluirPaciente(event: Event, paciente: Paciente): void {
    event.stopPropagation();

    this.erro = '';
    this.sucesso = '';

    const confirmar = confirm(`Confirma a exclusão do paciente "${paciente.nome}"?`);

    if (!confirmar) {
      return;
    }

    this.pacienteService.excluir(paciente.id).subscribe({
      next: () => {
        this.sucesso = 'Paciente excluído com sucesso';
        this.carregarPacientes();
      },
      error: () => {
        this.erro = 'Erro ao excluir paciente';
      }
    });
  }

  abrirAtestado(event: Event, pacienteId: number): void {
    event.stopPropagation();

    this.erro = '';
    this.sucesso = '';
    this.pacienteAtestadoId = pacienteId;
    this.exibindoAtestado = true;

    this.textoAtestado =
      'Atesto para os devidos fins que o paciente compareceu ao atendimento odontológico nesta data.';
  }

  cancelarAtestado(): void {
    this.exibindoAtestado = false;
    this.pacienteAtestadoId = undefined;
    this.textoAtestado = '';
    this.gerandoAtestado = false;
  }

  gerarAtestado(): void {
    this.erro = '';
    this.sucesso = '';

    if (!this.pacienteAtestadoId) {
      this.erro = 'Paciente não identificado para gerar atestado';
      return;
    }

    if (!this.textoAtestado.trim()) {
      this.erro = 'Informe o texto do atestado';
      return;
    }

    this.gerandoAtestado = true;

    this.documentoService
      .gerarAtestado(this.pacienteAtestadoId, this.textoAtestado.trim())
      .subscribe({
        next: (pdf) => {
          this.gerandoAtestado = false;

          const blob = new Blob([pdf], { type: 'application/pdf' });
          const url = window.URL.createObjectURL(blob);

          window.open(url, '_blank');

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
}