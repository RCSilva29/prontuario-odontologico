import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoginRequest, LoginResponse } from '../models/login.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly apiUrl = 'http://localhost:8080/auth/login';

  constructor(private http: HttpClient) { }

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.apiUrl, request);
  }

  salvarUsuario(usuario: LoginResponse): void {
    localStorage.setItem('usuarioLogado', JSON.stringify(usuario));
  }

  obterUsuario(): LoginResponse | null {
    const dados = localStorage.getItem('usuarioLogado');
    return dados ? JSON.parse(dados) : null;
  }

  logout(): void {
    localStorage.removeItem('usuarioLogado');
  }

  estaLogado(): boolean {
    return !!this.obterUsuario();
  }

  isAdmin(): boolean {
    return this.obterUsuario()?.perfil === 'ADMIN';
  }

  trocarSenhaObrigatoria(request: {
    email: string;
    novaSenha: string;
  }) {
    return this.http.put<void>(
      'http://localhost:8080/auth/troca-senha-obrigatoria',
      request
    );
  }
}