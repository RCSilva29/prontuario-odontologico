import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoginRequest, LoginResponse } from '../models/login.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly apiUrl = 'http://localhost:8080/auth/login';
  private readonly chaveUsuario = 'usuarioLogado';
  private readonly chaveUltimaAtividade = 'ultimaAtividade';
  private readonly tempoMaximoInatividadeMs = 60 * 60 * 1000; // 1 hora

  constructor(private http: HttpClient) { }

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.apiUrl, request);
  }

  salvarUsuario(usuario: LoginResponse): void {
    sessionStorage.setItem(this.chaveUsuario, JSON.stringify(usuario));
    this.registrarAtividade();
  }

  obterUsuario(): LoginResponse | null {
    const dados = sessionStorage.getItem(this.chaveUsuario);
    return dados ? JSON.parse(dados) : null;
  }

  logout(): void {
    sessionStorage.removeItem(this.chaveUsuario);
    sessionStorage.removeItem(this.chaveUltimaAtividade);
  }

  estaLogado(): boolean {
    const usuario = this.obterUsuario();

    if (!usuario) {
      return false;
    }

    if (this.tokenExpirado() || this.sessaoExpiradaPorInatividade()) {
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

  registrarAtividade(): void {
    if (!this.obterUsuario()) {
      return;
    }

    sessionStorage.setItem(this.chaveUltimaAtividade, String(Date.now()));
  }

  sessaoExpiradaPorInatividade(): boolean {
    const usuario = this.obterUsuario();

    if (!usuario) {
      return true;
    }

    const ultimaAtividade = sessionStorage.getItem(this.chaveUltimaAtividade);

    if (!ultimaAtividade) {
      return true;
    }

    const ultimaAtividadeMs = Number(ultimaAtividade);

    if (Number.isNaN(ultimaAtividadeMs)) {
      return true;
    }

    return Date.now() - ultimaAtividadeMs > this.tempoMaximoInatividadeMs;
  }
}
