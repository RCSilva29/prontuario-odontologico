import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login implements OnInit {

  email = '';
  senha = '';
  erro = '';
  carregando = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.email = this.route.snapshot.queryParamMap.get('email') || '';
  }

  entrar(): void {
    this.erro = '';

    if (!this.email.trim()) {
      this.erro = 'Informe o email';
      return;
    }

    if (!this.senha.trim()) {
      this.erro = 'Informe a senha';
      return;
    }

    this.carregando = true;

    this.authService.login({
      email: this.email.trim(),
      senha: this.senha
    }).subscribe({
      next: (usuario) => {
        this.authService.salvarUsuario(usuario);
        this.carregando = false;
        this.router.navigate(['/pacientes']);
      },

      error: (erro) => {
        this.carregando = false;

        const mensagem =
          erro?.error?.erro ||
          erro?.error?.message ||
          erro?.error ||
          '';

        if (typeof mensagem === 'string' && mensagem.includes('USUARIO_BLOQUEADO')) {
          this.authService.logout();
          this.erro = 'Usuário bloqueado. Procure o administrador.';
          return;
        }

        if (typeof mensagem === 'string' && mensagem.includes('TROCA_SENHA_OBRIGATORIA')) {
          this.authService.logout();

          this.router.navigate(['/minha-senha'], {
            queryParams: {
              obrigatoria: true,
              email: this.email.trim()
            }
          });

          return;
        }

        if (typeof mensagem === 'string' && mensagem.includes('Usuário inativo')) {
          this.authService.logout();
          this.erro = 'Usuário inativo. Procure o administrador.';
          return;
        }

        this.authService.logout();
        this.erro = 'Email ou senha inválidos';
      }
    });
  }
}