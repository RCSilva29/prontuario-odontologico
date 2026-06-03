import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProntuarioPdfRequest {
    incluirAnamnese: boolean;
    incluirOdontograma: boolean;
    incluirAtendimentos: boolean;
}

@Injectable({
    providedIn: 'root'
})
export class DocumentoService {

    private readonly apiUrl = 'http://localhost:8080/documentos';

    constructor(private http: HttpClient) { }

    gerarAtestado(
        pacienteId: number,
        texto: string
    ): Observable<Blob> {
        return this.http.post(
            `${this.apiUrl}/pacientes/${pacienteId}/atestado`,
            { texto },
            { responseType: 'blob' }
        );
    }

    gerarReceituario(
        pacienteId: number,
        prescricao: string,
        orientacoes: string
    ): Observable<Blob> {
        return this.http.post(
            `${this.apiUrl}/pacientes/${pacienteId}/receituario`,
            { prescricao, orientacoes },
            { responseType: 'blob' }
        );
    }

    gerarProntuario(
        pacienteId: number,
        request: ProntuarioPdfRequest
    ): Observable<Blob> {
        return this.http.post(
            `${this.apiUrl}/pacientes/${pacienteId}/prontuario`,
            request,
            { responseType: 'blob' }
        );
    }
}