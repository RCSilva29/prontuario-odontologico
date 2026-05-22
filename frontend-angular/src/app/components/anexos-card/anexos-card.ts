import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Anexo } from '../../models/anexo.model';
import { AnexoService } from '../../services/anexo.service';

@Component({
  selector: 'app-anexos-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './anexos-card.html',
  styleUrl: './anexos-card.scss'
})
export class AnexosCard implements OnInit {

  @Input({ required: true }) pacienteId!: number;

  anexos: Anexo[] = [];

  carregando = false;
  enviando = false;
  erro = '';

  constructor(private anexoService: AnexoService) {}

  ngOnInit(): void {
    this.carregarAnexos();
  }

  carregarAnexos(): void {
    this.carregando = true;

    this.anexoService.listar(this.pacienteId).subscribe({
      next: (dados) => {
        this.anexos = dados;
        this.carregando = false;
      },
      error: () => {
        this.erro = 'Erro ao carregar anexos';
        this.carregando = false;
      }
    });
  }

  selecionarArquivo(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files?.length) {
      return;
    }

    const arquivo = input.files[0];

    this.enviando = true;

    this.anexoService.upload(this.pacienteId, arquivo).subscribe({
      next: () => {
        this.enviando = false;
        this.carregarAnexos();
        input.value = '';
      },
      error: () => {
        this.erro = 'Erro ao enviar arquivo';
        this.enviando = false;
      }
    });
  }

  download(anexo: Anexo): void {
    window.open(
      this.anexoService.download(this.pacienteId, anexo.id),
      '_blank'
    );
  }

  excluir(anexo: Anexo): void {
    const confirmar = confirm(
      `Confirma excluir o arquivo ${anexo.nomeArquivo}?`
    );

    if (!confirmar) {
      return;
    }

    this.anexoService.excluir(this.pacienteId, anexo.id).subscribe({
      next: () => {
        this.carregarAnexos();
      },
      error: () => {
        this.erro = 'Erro ao excluir arquivo';
      }
    });
  }

  formatarData(data: string): string {
    return new Date(data).toLocaleString('pt-BR');
  }

  formatarTamanho(bytes: number): string {

    if (!bytes) {
      return '0 KB';
    }

    const kb = bytes / 1024;

    if (kb < 1024) {
      return `${kb.toFixed(1)} KB`;
    }

    return `${(kb / 1024).toFixed(1)} MB`;
  }
}