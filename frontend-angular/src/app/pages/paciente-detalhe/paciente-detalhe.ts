import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Paciente } from '../../models/paciente.model';
import { PacienteService } from '../../services/paciente.service';
import { AnamneseCard } from '../../components/anamnese-card/anamnese-card';
import { AtendimentosCard } from '../../components/atendimentos-card/atendimentos-card';
import { OdontogramaCard } from '../../components/odontograma-card/odontograma-card';
import { AnexosCard } from '../../components/anexos-card/anexos-card';

@Component({
  selector: 'app-paciente-detalhe',
  standalone: true,
  imports: [CommonModule, RouterLink, AnamneseCard, AtendimentosCard, OdontogramaCard, AnexosCard],
  templateUrl: './paciente-detalhe.html',
  styleUrl: './paciente-detalhe.scss'
})
export class PacienteDetalhe implements OnInit {

  paciente?: Paciente;
  carregando = false;
  erro = '';

  constructor(
    private route: ActivatedRoute,
    private pacienteService: PacienteService,

  ) { }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.carregarPaciente(id);
  }

  carregarPaciente(id: number): void {
    this.carregando = true;

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

}