import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Consulta, ConsultaRequest } from '../models/consulta.model';

@Injectable({
  providedIn: 'root'
})
export class ConsultaService {

  private readonly apiUrl = 'http://localhost:8080/consultas';

  constructor(private http: HttpClient) { }

  listar(inicio: string, fim: string): Observable<Consulta[]> {
    return this.http.get<Consulta[]>(
      `${this.apiUrl}?inicio=${encodeURIComponent(inicio)}&fim=${encodeURIComponent(fim)}`
    );
  }

  criar(request: ConsultaRequest): Observable<Consulta> {
    return this.http.post<Consulta>(this.apiUrl, request);
  }

  atualizar(id: number, request: ConsultaRequest): Observable<Consulta> {
    return this.http.put<Consulta>(`${this.apiUrl}/${id}`, request);
  }

  cancelar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/cancelar`, {});
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}