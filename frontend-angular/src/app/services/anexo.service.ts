import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Anexo } from '../models/anexo.model';

@Injectable({
  providedIn: 'root'
})
export class AnexoService {

  private readonly apiBase = 'http://localhost:8080/pacientes';

  constructor(private http: HttpClient) { }

  listar(pacienteId: number): Observable<Anexo[]> {
    return this.http.get<Anexo[]>(
      `${this.apiBase}/${pacienteId}/anexos`
    );
  }

  upload(pacienteId: number, arquivo: File): Observable<Anexo> {
    const formData = new FormData();

    formData.append('arquivo', arquivo);

    return this.http.post<Anexo>(
      `${this.apiBase}/${pacienteId}/anexos`,
      formData
    );
  }

  download(pacienteId: number, anexoId: number): string {
    return `${this.apiBase}/${pacienteId}/anexos/${anexoId}/download`;
  }

  abrir(pacienteId: number, anexoId: number): Observable<Blob> {
    return this.http.get(
      `${this.apiBase}/${pacienteId}/anexos/${anexoId}/download`,
      {
        responseType: 'blob'
      }
    );
  }

  excluir(pacienteId: number, anexoId: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiBase}/${pacienteId}/anexos/${anexoId}`
    );
  }
}