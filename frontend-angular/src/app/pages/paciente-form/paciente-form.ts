import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PacienteService } from '../../services/paciente.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-paciente-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './paciente-form.html',
  styleUrl: './paciente-form.scss'
})
export class PacienteForm implements OnInit {

  paciente = {
    nome: '',
    cpf: '',
    dataNascimento: '',
    telefone: '',
    email: '',
    observacoes: ''
  };

  erro = '';
  erroEmail = '';
  salvando = false;

  idPaciente?: number;
  modoEdicao = false;

  constructor(
    private pacienteService: PacienteService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.idPaciente = Number(id);
      this.modoEdicao = true;
      this.carregarPaciente(this.idPaciente);
    }
  }

  carregarPaciente(id: number): void {
    this.pacienteService.buscarPorId(id).subscribe({
      next: (paciente) => {
        this.paciente = {
          nome: paciente.nome || '',
          cpf: this.formatarCpfValor(paciente.cpf || ''),
          dataNascimento: paciente.dataNascimento || '',
          telefone: this.formatarTelefoneValor(paciente.telefone || ''),
          email: paciente.email || '',
          observacoes: paciente.observacoes || ''
        };
      },
      error: () => {
        this.erro = 'Erro ao carregar paciente';
      }
    });
  }

  salvar(): void {
    this.erro = '';
    this.erroEmail = '';

    const nome = this.paciente.nome?.trim();
    const cpf = this.paciente.cpf?.trim();
    const email = this.paciente.email?.trim();

    if (!nome) {
      this.erro = 'O campo Nome é obrigatório';
      return;
    }

    if (!cpf) {
      this.erro = 'O campo CPF é obrigatório';
      return;
    }

    const cpfSomenteNumeros = cpf.replace(/\D/g, '');

    if (cpfSomenteNumeros.length !== 11) {
      this.erro = 'CPF deve conter 11 números';
      return;
    }

    if (email && !this.emailValido(email)) {
      this.erroEmail = 'Email inválido';
      return;
    }

    this.salvando = true;

    const pacienteParaSalvar = {
      ...this.paciente,
      cpf: cpfSomenteNumeros,
      telefone: this.paciente.telefone.replace(/\D/g, ''),
      email: email || ''
    };

    const requisicao = this.modoEdicao && this.idPaciente
      ? this.pacienteService.atualizar(this.idPaciente, pacienteParaSalvar)
      : this.pacienteService.cadastrar(pacienteParaSalvar);

    requisicao.subscribe({
      next: () => {
        this.salvando = false;
        this.router.navigate(['/pacientes']);
      },
      error: () => {
        this.erro = this.modoEdicao
          ? 'Erro ao atualizar paciente'
          : 'Erro ao cadastrar paciente';

        this.salvando = false;
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/pacientes']);
  }

  formatarCpf(): void {
    this.paciente.cpf = this.formatarCpfValor(this.paciente.cpf);
  }

  formatarTelefone(): void {
    this.paciente.telefone = this.formatarTelefoneValor(this.paciente.telefone);
  }

  formatarCpfValor(cpf: string): string {
    let valor = (cpf || '').replace(/\D/g, '');

    if (valor.length > 11) {
      valor = valor.substring(0, 11);
    }

    if (valor.length > 9) {
      return valor.replace(/^(\d{3})(\d{3})(\d{3})(\d{0,2}).*/, '$1.$2.$3-$4');
    }

    if (valor.length > 6) {
      return valor.replace(/^(\d{3})(\d{3})(\d{0,3}).*/, '$1.$2.$3');
    }

    if (valor.length > 3) {
      return valor.replace(/^(\d{3})(\d{0,3}).*/, '$1.$2');
    }

    return valor;
  }

  formatarTelefoneValor(telefone: string): string {
    let valor = (telefone || '').replace(/\D/g, '');

    if (valor.length > 11) {
      valor = valor.substring(0, 11);
    }

    if (valor.length === 0) {
      return '';
    }

    if (valor.length <= 10) {
      return valor.replace(/^(\d{2})(\d{0,4})(\d{0,4}).*/, (_m, ddd, parte1, parte2) => {
        let retorno = `(${ddd}`;

        if (ddd.length === 2) {
          retorno += ')';
        }

        if (parte1) {
          retorno += ` ${parte1}`;
        }

        if (parte2) {
          retorno += `-${parte2}`;
        }

        return retorno;
      });
    }

    return valor.replace(/^(\d{2})(\d{5})(\d{0,4}).*/, '$1 $2-$3')
      .replace(/^(\d{2}) /, '($1) ');
  }

  emailValido(email: string): boolean {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
  }

  permitirSomenteNumeros(event: KeyboardEvent): void {
    const tecla = event.key;

    if (!/^\d$/.test(tecla)) {
      event.preventDefault();
    }
  }

  bloquearColarNaoNumerico(event: ClipboardEvent): void {
    const textoColado = event.clipboardData?.getData('text') || '';

    if (!/^\d+$/.test(textoColado)) {
      event.preventDefault();
    }
  }

  validarEmailCampo(): void {
    this.erroEmail = '';

    const email = this.paciente.email?.trim();

    if (email && !this.emailValido(email)) {
      this.erroEmail = 'Email inválido';
    }
  }
}