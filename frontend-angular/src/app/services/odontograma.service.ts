import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Odontograma, OdontogramaRequest } from '../models/odontograma.model';

@Injectable({
  providedIn: 'root'
})
export class OdontogramaService {

  private readonly apiBase = 'http://localhost:8080/pacientes';

  constructor(private http: HttpClient) {}

  listarPorPaciente(pacienteId: number): Observable<Odontograma[]> {
    return this.http.get<Odontograma[]>(`${this.apiBase}/${pacienteId}/odontograma`);
  }

  cadastrar(pacienteId: number, request: OdontogramaRequest): Observable<Odontograma> {
    return this.http.post<Odontograma>(`${this.apiBase}/${pacienteId}/odontograma`, request);
  }

  atualizar(pacienteId: number, odontogramaId: number, request: OdontogramaRequest): Observable<Odontograma> {
    return this.http.put<Odontograma>(
      `${this.apiBase}/${pacienteId}/odontograma/${odontogramaId}`,
      request
    );
  }

  excluir(pacienteId: number, odontogramaId: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiBase}/${pacienteId}/odontograma/${odontogramaId}`
    );
  }
}