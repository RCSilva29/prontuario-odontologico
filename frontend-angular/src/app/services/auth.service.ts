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
    const usuario = this.obterUsuario();

    if (!usuario) {
      return false;
    }

    if (this.tokenExpirado()) {
      this.logout();
      return false;
    }

    return true;
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

  tokenExpirado(): boolean {
    const usuario = this.obterUsuario();

    if (!usuario?.token) {
      return true;
    }

    try {
      const payloadBase64 = usuario.token.split('.')[1];

      if (!payloadBase64) {
        return true;
      }

      const payloadJson = atob(payloadBase64);
      const payload = JSON.parse(payloadJson);

      if (!payload.exp) {
        return true;
      }

      const agoraEmSegundos = Math.floor(Date.now() / 1000);

      return payload.exp < agoraEmSegundos;
    } catch {
      return true;
    }
  }

  sessaoValida(): boolean {
    return this.estaLogado() && !this.tokenExpirado();
  }
}