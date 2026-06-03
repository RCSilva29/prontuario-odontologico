import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Atendimento, AtendimentoRequest } from '../../models/atendimento.model';
import { AtendimentoService } from '../../services/atendimento.service';

@Component({
  selector: 'app-atendimentos-card',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './atendimentos-card.html',
  styleUrl: './atendimentos-card.scss'
})
export class AtendimentosCard implements OnInit {

  @Input({ required: true }) pacienteId!: number;

  atendimentos: Atendimento[] = [];
  atendimentoEditando?: Atendimento;

  carregando = false;
  editando = false;
  erro = '';

  form: AtendimentoRequest = {
    queixaPrincipal: '',
    evolucaoClinica: '',
    procedimentoRealizado: '',
    observacoes: ''
  };

  constructor(private atendimentoService: AtendimentoService) { }

  ngOnInit(): void {
    this.carregarAtendimentos();
  }

  carregarAtendimentos(): void {
    this.carregando = true;
    this.erro = '';

    this.atendimentoService.listarPorPaciente(this.pacienteId).subscribe({
      next: (dados) => {
        this.atendimentos = dados.sort((a, b) => {
          const dataA = new Date(a.dataAtendimento || '').getTime();
          const dataB = new Date(b.dataAtendimento || '').getTime();

          return dataB - dataA;
        });

        this.carregando = false;
      },
      error: () => {
        this.erro = 'Erro ao carregar atendimentos';
        this.carregando = false;
      }
    });
  }

  novo(): void {
    this.erro = '';
    this.editando = true;
    this.atendimentoEditando = undefined;

    this.form = {
      queixaPrincipal: '',
      evolucaoClinica: '',
      procedimentoRealizado: '',
      observacoes: ''
    };
  }

  editar(atendimento: Atendimento): void {
    this.erro = '';
    this.editando = true;
    this.atendimentoEditando = atendimento;

    this.form = {
      queixaPrincipal: atendimento.queixaPrincipal || '',
      evolucaoClinica: atendimento.evolucaoClinica || '',
      procedimentoRealizado: atendimento.procedimentoRealizado || '',
      observacoes: atendimento.observacoes || ''
    };
  }

  salvar(): void {
    this.erro = '';

    const requisicao = this.atendimentoEditando
      ? this.atendimentoService.atualizar(this.pacienteId, this.atendimentoEditando.id, this.form)
      : this.atendimentoService.cadastrar(this.pacienteId, this.form);

    requisicao.subscribe({
      next: () => {
        this.editando = false;
        this.atendimentoEditando = undefined;
        this.carregarAtendimentos();
      },
      error: () => {
        this.erro = 'Erro ao salvar atendimento';
      }
    });
  }

  cancelar(): void {
    this.editando = false;
    this.atendimentoEditando = undefined;
    this.erro = '';

    this.form = {
      queixaPrincipal: '',
      evolucaoClinica: '',
      procedimentoRealizado: '',
      observacoes: ''
    };
  }

  formatarDataHora(data: string): string {
    if (!data) {
      return '-';
    }

    const dataObj = new Date(data);

    if (isNaN(dataObj.getTime())) {
      return data;
    }

    return dataObj.toLocaleDateString('pt-BR');
  }
}