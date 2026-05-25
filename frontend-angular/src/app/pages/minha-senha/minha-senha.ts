import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { UsuarioService } from '../../services/usuario.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-minha-senha',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './minha-senha.html',
  styleUrl: './minha-senha.scss'
})
export class MinhaSenha implements OnInit {

  email = '';
  senhaAtual = '';
  novaSenha = '';
  confirmarSenha = '';

  obrigatoria = false;

  erro = '';
  sucesso = '';
  salvando = false;

  constructor(
    private usuarioService: UsuarioService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.obrigatoria = this.route.snapshot.queryParamMap.get('obrigatoria') === 'true';
    this.email = this.route.snapshot.queryParamMap.get('email') || '';

    if (this.obrigatoria) {
      this.authService.logout();

      if (!this.email) {
        this.router.navigate(['/login']);
      }

      return;
    }

    const usuario = this.authService.obterUsuario();

    if (!usuario) {
      this.router.navigate(['/login']);
      return;
    }

    this.email = usuario.email;
  }

  salvar(): void {
    this.erro = '';
    this.sucesso = '';

    if (!this.obrigatoria && !this.senhaAtual.trim()) {
      this.erro = 'Informe a senha atual';
      return;
    }

    if (!this.novaSenha.trim()) {
      this.erro = 'Informe a nova senha';
      return;
    }

    if (this.novaSenha.length < 6) {
      this.erro = 'A nova senha deve ter no mínimo 6 caracteres';
      return;
    }

    if (!this.obrigatoria && this.novaSenha === this.senhaAtual) {
      this.erro = 'A nova senha deve ser diferente da senha atual';
      return;
    }

    if (this.novaSenha !== this.confirmarSenha) {
      this.erro = 'A confirmação da senha não confere';
      return;
    }

    this.salvando = true;

    if (this.obrigatoria) {
      this.trocarSenhaObrigatoria();
      return;
    }

    this.trocarSenhaLogado();
  }

  private trocarSenhaObrigatoria(): void {
    this.authService.trocarSenhaObrigatoria({
      email: this.email,
      novaSenha: this.novaSenha
    }).subscribe({
      next: () => {
        this.salvando = false;

        this.router.navigate(['/login'], {
          queryParams: {
            email: this.email
          }
        });
      },
      error: () => {
        this.erro = 'Erro ao alterar senha. Verifique a senha atual.';
        this.salvando = false;
      }
    });
  }

  private trocarSenhaLogado(): void {
    const usuario = this.authService.obterUsuario();

    if (!usuario) {
      this.router.navigate(['/login']);
      return;
    }

    this.usuarioService.alterarSenha(usuario.id, {
      senhaAtual: this.senhaAtual,
      novaSenha: this.novaSenha
    }).subscribe({
      next: () => {
        this.salvando = false;
        this.sucesso = 'Senha alterada com sucesso';
        this.senhaAtual = '';
        this.novaSenha = '';
        this.confirmarSenha = '';
      },
      error: () => {
        this.erro = 'Erro ao alterar senha. Verifique a senha atual.';
        this.salvando = false;
      }
    });
  }

  cancelar(): void {
    if (this.obrigatoria) {
      this.authService.logout();
      this.router.navigate(['/login'], {
        queryParams: {
          email: this.email
        }
      });
      return;
    }

    this.router.navigate(['/pacientes']);
  }
}