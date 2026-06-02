import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Paciente } from '../models/paciente.model';

export interface PacienteRequest {
  nome: string;
  cpf?: string;
  dataNascimento?: string;
  telefone?: string;
  email?: string;
  observacoes?: string;
}

@Injectable({
  providedIn: 'root'
})
export class PacienteService {

  private readonly apiUrl = 'http://localhost:8080/pacientes';

  constructor(private http: HttpClient) { }

  listar(termo?: string): Observable<Paciente[]> {
    let params = new HttpParams();

    if (termo && termo.trim()) {
      params = params.set('termo', termo.trim());
    }

    return this.http.get<Paciente[]>(this.apiUrl, { params });
  }

  cadastrar(paciente: PacienteRequest): Observable<Paciente> {
    return this.http.post<Paciente>(this.apiUrl, paciente);
  }

  buscarPorId(id: number): Observable<Paciente> {
    return this.http.get<Paciente>(`${this.apiUrl}/${id}`);
  }

  atualizar(id: number, paciente: PacienteRequest): Observable<Paciente> {
    return this.http.put<Paciente>(`${this.apiUrl}/${id}`, paciente);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}