import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Anamnese, AnamneseRequest } from '../../models/anamnese.model';
import { AnamneseService } from '../../services/anamnese.service';

@Component({
  selector: 'app-anamnese-card',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './anamnese-card.html',
  styleUrl: './anamnese-card.scss'
})
export class AnamneseCard implements OnInit {

  @Input({ required: true }) pacienteId!: number;

  anamnese?: Anamnese;
  carregando = false;
  editando = false;
  erro = '';

  form: AnamneseRequest = {
    hipertensao: false,
    diabetes: false,
    alergias: '',
    medicamentos: '',
    fumante: false,
    gravida: false,
    observacoes: ''
  };

  constructor(private anamneseService: AnamneseService) {}

  ngOnInit(): void {
    this.carregarAnamnese();
  }

  carregarAnamnese(): void {
    this.carregando = true;

    this.anamneseService.buscarPorPaciente(this.pacienteId).subscribe({
      next: (dados) => {
        this.anamnese = dados;
        this.preencherForm(dados);
        this.carregando = false;
      },
      error: () => {
        this.anamnese = undefined;
        this.carregando = false;
      }
    });
  }

  novaOuEditar(): void {
    this.editando = true;

    if (this.anamnese) {
      this.preencherForm(this.anamnese);
    }
  }

  salvar(): void {
    this.erro = '';

    const requisicao = this.anamnese
      ? this.anamneseService.atualizar(this.pacienteId, this.form)
      : this.anamneseService.cadastrar(this.pacienteId, this.form);

    requisicao.subscribe({
      next: (dados) => {
        this.anamnese = dados;
        this.preencherForm(dados);
        this.editando = false;
      },
      error: () => {
        this.erro = 'Erro ao salvar anamnese';
      }
    });
  }

  cancelar(): void {
    this.editando = false;

    if (this.anamnese) {
      this.preencherForm(this.anamnese);
    }
  }

  private preencherForm(anamnese: Anamnese): void {
    this.form = {
      hipertensao: anamnese.hipertensao,
      diabetes: anamnese.diabetes,
      alergias: anamnese.alergias || '',
      medicamentos: anamnese.medicamentos || '',
      fumante: anamnese.fumante,
      gravida: anamnese.gravida,
      observacoes: anamnese.observacoes || ''
    };
  }
}