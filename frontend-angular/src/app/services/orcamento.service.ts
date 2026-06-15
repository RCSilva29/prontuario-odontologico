import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Orcamento,
  OrcamentoPagamentoRequest,
  OrcamentoRequest
} from '../models/orcamento.model';

@Injectable({
  providedIn: 'root'
})
export class OrcamentoService {

  private readonly apiUrl = 'http://localhost:8080/orcamentos';

  constructor(private http: HttpClient) { }

  listarPorPaciente(pacienteId: number): Observable<Orcamento[]> {
    return this.http.get<Orcamento[]>(`${this.apiUrl}/pacientes/${pacienteId}`);
  }

  buscarPorId(id: number): Observable<Orcamento> {
    return this.http.get<Orcamento>(`${this.apiUrl}/${id}`);
  }

  criar(pacienteId: number, request: OrcamentoRequest): Observable<Orcamento> {
    return this.http.post<Orcamento>(`${this.apiUrl}/pacientes/${pacienteId}`, request);
  }

  atualizar(id: number, request: OrcamentoRequest): Observable<Orcamento> {
    return this.http.put<Orcamento>(`${this.apiUrl}/${id}`, request);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  registrarPagamento(orcamentoId: number, request: OrcamentoPagamentoRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${orcamentoId}/pagamentos`, request);
  }

  excluirPagamento(orcamentoId: number, pagamentoId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${orcamentoId}/pagamentos/${pagamentoId}`);
  }
}