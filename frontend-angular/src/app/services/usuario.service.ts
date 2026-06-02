import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Usuario, UsuarioRequest } from '../models/usuario.model';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  private readonly apiUrl = 'http://localhost:8080/usuarios';

  constructor(private http: HttpClient) { }

  listar(termo?: string): Observable<Usuario[]> {
    let params = new HttpParams();

    if (termo && termo.trim()) {
      params = params.set('termo', termo.trim());
    }

    return this.http.get<Usuario[]>(this.apiUrl, { params });
  }

  cadastrar(request: UsuarioRequest): Observable<Usuario> {
    return this.http.post<Usuario>(this.apiUrl, request);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  buscarPorId(id: number): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.apiUrl}/${id}`);
  }

  atualizar(id: number, request: UsuarioRequest): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.apiUrl}/${id}`, request);
  }

  alterarSenha(id: number, request: { senhaAtual: string; novaSenha: string }): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/senha`, request);
  }

  desbloquear(id: number): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.apiUrl}/${id}/desbloquear`, {});
  }

  redefinirSenha(id: number, novaSenha: string): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.apiUrl}/${id}/redefinir-senha`, {
      novaSenha
    });
  }

  reativar(id: number): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.apiUrl}/${id}/reativar`, {});
  }
}