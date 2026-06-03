import { Component, OnDestroy, OnInit } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit, OnDestroy {

  private verificadorSessao?: ReturnType<typeof setInterval>;
  private readonly eventosAtividade = [
    'click',
    'keydown',
    'mousemove',
    'scroll',
    'touchstart'
  ];

  private readonly registrarAtividadeUsuario = () => {
    if (this.authService.estaLogado()) {
      this.authService.registrarAtividade();
    }
  };

  constructor(
    public router: Router,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.eventosAtividade.forEach((evento) => {
      window.addEventListener(evento, this.registrarAtividadeUsuario, true);
    });

    this.verificadorSessao = setInterval(() => {
      if (!this.isLoginPage() && !this.authService.sessaoValida()) {
        this.authService.logout();
        this.router.navigate(['/login']);
      }
    }, 60 * 1000);
  }

  ngOnDestroy(): void {
    this.eventosAtividade.forEach((evento) => {
      window.removeEventListener(evento, this.registrarAtividadeUsuario, true);
    });

    if (this.verificadorSessao) {
      clearInterval(this.verificadorSessao);
    }
  }

  isLoginPage(): boolean {
    return this.router.url.startsWith('/login')
      || this.router.url.startsWith('/minha-senha?obrigatoria=true');
  }

  nomeUsuario(): string {
    const usuario = this.authService.obterUsuario();

    if (!usuario?.nome) {
      return 'Usuário';
    }

    return usuario.nome;
  }

  iniciaisUsuario(): string {
    const nome = this.nomeUsuario().trim();

    return nome
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map(parte => parte[0].toUpperCase())
      .join('');
  }

  sair(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }
}
