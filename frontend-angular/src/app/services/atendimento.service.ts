import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Atendimento, AtendimentoRequest } from '../models/atendimento.model';

@Injectable({
  providedIn: 'root'
})
export class AtendimentoService {

  private readonly apiBase = 'http://localhost:8080/pacientes';

  constructor(private http: HttpClient) {}

  listarPorPaciente(pacienteId: number): Observable<Atendimento[]> {
    return this.http.get<Atendimento[]>(`${this.apiBase}/${pacienteId}/atendimentos`);
  }

  cadastrar(pacienteId: number, request: AtendimentoRequest): Observable<Atendimento> {
    return this.http.post<Atendimento>(`${this.apiBase}/${pacienteId}/atendimentos`, request);
  }

  atualizar(pacienteId: number, atendimentoId: number, request: AtendimentoRequest): Observable<Atendimento> {
    return this.http.put<Atendimento>(
      `${this.apiBase}/${pacienteId}/atendimentos/${atendimentoId}`,
      request
    );
  }
}