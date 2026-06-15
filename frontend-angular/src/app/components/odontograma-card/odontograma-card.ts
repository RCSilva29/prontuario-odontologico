import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { Odontograma, OdontogramaRequest } from '../../models/odontograma.model';
import { OdontogramaService } from '../../services/odontograma.service';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-odontograma-card',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './odontograma-card.html',
  styleUrl: './odontograma-card.scss'
})
export class OdontogramaCard implements OnInit {

  @Input({ required: true }) pacienteId!: number;

  @Output() adicionarAoOrcamento = new EventEmitter<string[]>();

  superioresEsquerdo = ['18', '17', '16', '15', '14', '13', '12', '11'];
  superioresDireito = ['21', '22', '23', '24', '25', '26', '27', '28'];
  inferioresEsquerdo = ['48', '47', '46', '45', '44', '43', '42', '41'];
  inferioresDireito = ['31', '32', '33', '34', '35', '36', '37', '38'];

  statusOptions = ['Saudável', 'Cariado', 'Restaurado', 'Canal', 'Extraído', 'Implante', 'Prótese', 'Ausente'];

  registros: Odontograma[] = [];
  dentesSelecionados: string[] = [];

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

    if (this.dentesSelecionados.includes(numeroDente)) {
      this.dentesSelecionados = this.dentesSelecionados.filter(dente => dente !== numeroDente);
    } else {
      this.dentesSelecionados = [...this.dentesSelecionados, numeroDente];
    }

    this.preencherFormularioPeloPrimeiroDenteSelecionado();
  }

  selecionarArcadaSuperior(): void {
    this.definirSelecao([...this.superioresEsquerdo, ...this.superioresDireito]);
  }

  selecionarArcadaInferior(): void {
    this.definirSelecao([...this.inferioresEsquerdo, ...this.inferioresDireito]);
  }

  selecionarTodos(): void {
    this.definirSelecao(this.todosOsDentes());
  }

  limparSelecao(): void {
    this.dentesSelecionados = [];
    this.erro = '';
    this.salvando = false;

    this.form = {
      numeroDente: '',
      status: '',
      observacao: ''
    };
  }

  definirSelecao(dentes: string[]): void {
    this.erro = '';
    this.dentesSelecionados = dentes;
    this.preencherFormularioPeloPrimeiroDenteSelecionado();
  }

  preencherFormularioPeloPrimeiroDenteSelecionado(): void {
    const primeiroDente = this.dentesSelecionados[0];

    if (!primeiroDente) {
      this.form = {
        numeroDente: '',
        status: '',
        observacao: ''
      };
      return;
    }

    const registro = this.buscarRegistroPorDente(primeiroDente);

    this.form = {
      numeroDente: primeiroDente,
      status: registro?.status || '',
      observacao: registro?.observacao || ''
    };
  }

  salvar(): void {
    this.erro = '';

    if (this.dentesSelecionados.length === 0) {
      this.erro = 'Selecione ao menos um dente';
      return;
    }

    if (!this.form.status) {
      this.erro = 'Selecione o status do dente';
      return;
    }

    this.salvando = true;

    const requisicoes = this.dentesSelecionados.map(numeroDente => {
      const registroExistente = this.buscarRegistroPorDente(numeroDente);

      const request: OdontogramaRequest = {
        numeroDente,
        status: this.form.status,
        observacao: this.form.observacao
      };

      return registroExistente
        ? this.odontogramaService.atualizar(this.pacienteId, registroExistente.id, request)
        : this.odontogramaService.cadastrar(this.pacienteId, request);
    });

    forkJoin(requisicoes).subscribe({
      next: () => {
        this.salvando = false;
        this.limparSelecao();
        this.carregarRegistros();
      },
      error: () => {
        this.erro = 'Erro ao salvar odontograma';
        this.salvando = false;
      }
    });
  }

  limparRegistro(): void {
    this.erro = '';

    if (this.dentesSelecionados.length === 0) {
      this.erro = 'Selecione ao menos um dente';
      return;
    }

    const registrosParaExcluir = this.dentesSelecionados
      .map(dente => this.buscarRegistroPorDente(dente))
      .filter((registro): registro is Odontograma => !!registro);

    if (registrosParaExcluir.length === 0) {
      this.form.status = '';
      this.form.observacao = '';
      return;
    }

    const confirmar = confirm(`Confirma excluir o registro de ${registrosParaExcluir.length} dente(s) selecionado(s)?`);

    if (!confirmar) {
      return;
    }

    const requisicoes = registrosParaExcluir.map(registro =>
      this.odontogramaService.excluir(this.pacienteId, registro.id)
    );

    forkJoin(requisicoes.length > 0 ? requisicoes : [of(null)]).subscribe({
      next: () => {
        this.limparSelecao();
        this.carregarRegistros();
      },
      error: () => {
        this.erro = 'Erro ao excluir registro';
      }
    });
  }

  cancelarSelecao(): void {
    this.limparSelecao();
  }

  buscarRegistroPorDente(numeroDente: string): Odontograma | undefined {
    return this.registros.find(registro => registro.numeroDente === numeroDente);
  }

  possuiRegistro(numeroDente: string): boolean {
    return !!this.buscarRegistroPorDente(numeroDente);
  }

  denteEstaSelecionado(numeroDente: string): boolean {
    return this.dentesSelecionados.includes(numeroDente);
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

  todosOsDentes(): string[] {
    return [
      ...this.superioresEsquerdo,
      ...this.superioresDireito,
      ...this.inferioresEsquerdo,
      ...this.inferioresDireito
    ];
  }

  textoSelecao(): string {
    if (this.dentesSelecionados.length === 0) {
      return '';
    }

    if (this.dentesSelecionados.length === 1) {
      return `Dente ${this.dentesSelecionados[0]}`;
    }

    return `${this.dentesSelecionados.length} dentes selecionados`;
  }

  enviarDentesParaOrcamento(): void {
    if (this.dentesSelecionados.length === 0) {
      this.erro = 'Selecione ao menos um dente para adicionar ao orçamento';
      return;
    }

    this.adicionarAoOrcamento.emit([...this.dentesSelecionados]);
  }
}