import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Odontograma, OdontogramaRequest } from '../../models/odontograma.model';
import { OdontogramaService } from '../../services/odontograma.service';

@Component({
  selector: 'app-odontograma-card',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './odontograma-card.html',
  styleUrl: './odontograma-card.scss'
})
export class OdontogramaCard implements OnInit {

  @Input({ required: true }) pacienteId!: number;

  superioresEsquerdo = ['18', '17', '16', '15', '14', '13', '12', '11'];
  superioresDireito = ['21', '22', '23', '24', '25', '26', '27', '28'];
  inferioresEsquerdo = ['48', '47', '46', '45', '44', '43', '42', '41'];
  inferioresDireito = ['31', '32', '33', '34', '35', '36', '37', '38'];

  statusOptions = [
    'Saudável',
    'Cariado',
    'Restaurado',
    'Canal',
    'Extraído',
    'Implante',
    'Prótese',
    'Ausente'
  ];

  registros: Odontograma[] = [];
  denteSelecionado = '';

  erro = '';
  salvando = false;
  carregando = false;

  form: OdontogramaRequest = {
    numeroDente: '',
    status: '',
    observacao: ''
  };

  constructor(private odontogramaService: OdontogramaService) { }

  ngOnInit(): void {
    this.carregarRegistros();
  }

  carregarRegistros(): void {
    this.carregando = true;
    this.erro = '';

    this.odontogramaService.listarPorPaciente(this.pacienteId).subscribe({
      next: (dados) => {
        this.registros = dados;
        this.carregando = false;
      },
      error: () => {
        this.erro = 'Erro ao carregar odontograma';
        this.carregando = false;
      }
    });
  }

  selecionarDente(numeroDente: string): void {
    this.erro = '';

    if (this.denteSelecionado === numeroDente) {
      this.cancelarSelecao();
      return;
    }

    this.denteSelecionado = numeroDente;

    const registro = this.buscarRegistroPorDente(numeroDente);

    this.form = {
      numeroDente,
      status: registro?.status || '',
      observacao: registro?.observacao || ''
    };
  }

  salvar(): void {
    this.erro = '';

    if (!this.denteSelecionado) {
      this.erro = 'Selecione um dente';
      return;
    }

    if (!this.form.status) {
      this.erro = 'Selecione o status do dente';
      return;
    }

    this.salvando = true;

    const registroExistente = this.buscarRegistroPorDente(this.denteSelecionado);

    const requisicao = registroExistente
      ? this.odontogramaService.atualizar(this.pacienteId, registroExistente.id, this.form)
      : this.odontogramaService.cadastrar(this.pacienteId, this.form);

    requisicao.subscribe({
      next: () => {
        this.salvando = false;
        this.cancelarSelecao();
        this.carregarRegistros();
      },
      error: () => {
        this.erro = 'Erro ao salvar odontograma';
        this.salvando = false;
      }
    });
  }

  limparRegistro(): void {
    const registroExistente = this.buscarRegistroPorDente(this.denteSelecionado);

    if (!registroExistente) {
      this.form.status = '';
      this.form.observacao = '';
      return;
    }

    const confirmar = confirm(`Confirma excluir o registro do dente ${this.denteSelecionado}?`);

    if (!confirmar) {
      return;
    }

    this.odontogramaService.excluir(this.pacienteId, registroExistente.id).subscribe({
      next: () => {
        this.cancelarSelecao();
        this.carregarRegistros();
      },
      error: () => {
        this.erro = 'Erro ao excluir registro';
      }
    });
  }

  cancelarSelecao(): void {
    this.denteSelecionado = '';
    this.erro = '';
    this.salvando = false;

    this.form = {
      numeroDente: '',
      status: '',
      observacao: ''
    };
  }

  buscarRegistroPorDente(numeroDente: string): Odontograma | undefined {
    return this.registros.find(registro => registro.numeroDente === numeroDente);
  }

  possuiRegistro(numeroDente: string): boolean {
    return !!this.buscarRegistroPorDente(numeroDente);
  }

  obterStatus(numeroDente: string): string {
    return this.buscarRegistroPorDente(numeroDente)?.status || '';
  }

  classeStatus(numeroDente: string): string {
    const status = this.obterStatus(numeroDente);

    return status
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toLowerCase()
      .replace(/\s+/g, '-');
  }
}