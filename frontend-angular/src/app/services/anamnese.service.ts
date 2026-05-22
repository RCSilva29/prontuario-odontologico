import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Anamnese, AnamneseRequest } from '../models/anamnese.model';

@Injectable({
  providedIn: 'root'
})
export class AnamneseService {

  private readonly apiBase = 'http://localhost:8080/pacientes';

  constructor(private http: HttpClient) {}

  buscarPorPaciente(pacienteId: number): Observable<Anamnese> {
    return this.http.get<Anamnese>(`${this.apiBase}/${pacienteId}/anamnese`);
  }

  cadastrar(pacienteId: number, request: AnamneseRequest): Observable<Anamnese> {
    return this.http.post<Anamnese>(`${this.apiBase}/${pacienteId}/anamnese`, request);
  }

  atualizar(pacienteId: number, request: AnamneseRequest): Observable<Anamnese> {
    return this.http.put<Anamnese>(`${this.apiBase}/${pacienteId}/anamnese`, request);
  }
}