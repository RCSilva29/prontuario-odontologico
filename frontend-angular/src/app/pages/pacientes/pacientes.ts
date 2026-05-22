import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PacienteService } from '../../services/paciente.service';
import { Paciente } from '../../models/paciente.model';

@Component({
  selector: 'app-pacientes',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './pacientes.html',
  styleUrl: './pacientes.scss'
})
export class Pacientes implements OnInit {

  pacientes: Paciente[] = [];
  carregando = false;
  erro = '';

  constructor(private pacienteService: PacienteService) {}

  ngOnInit(): void {
    this.carregarPacientes();
  }

  carregarPacientes(): void {
    this.carregando = true;

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

    const confirmar = confirm(`Confirma a exclusão do paciente "${paciente.nome}"?`);

    if (!confirmar) {
      return;
    }

    this.pacienteService.excluir(paciente.id).subscribe({
      next: () => {
        this.carregarPacientes();
      },
      error: () => {
        this.erro = 'Erro ao excluir paciente';
      }
    });
  }
}