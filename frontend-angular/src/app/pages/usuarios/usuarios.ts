import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Usuario, UsuarioRequest } from '../../models/usuario.model';
import { UsuarioService } from '../../services/usuario.service';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './usuarios.html',
  styleUrl: './usuarios.scss'
})
export class Usuarios implements OnInit {

  usuarios: Usuario[] = [];
  usuarioEditando?: Usuario;

  form: UsuarioRequest = {
    nome: '',
    email: '',
    senha: '',
    perfil: 'DENTISTA'
  };

  erro = '';
  sucesso = '';
  carregando = false;
  salvando = false;
  exibindoFormulario = false;

  constructor(private usuarioService: UsuarioService) { }

  ngOnInit(): void {
    this.carregarUsuarios();
  }

  carregarUsuarios(): void {
    this.carregando = true;

    this.usuarioService.listar().subscribe({
      next: (dados) => {
        this.usuarios = dados;
        this.carregando = false;
      },
      error: () => {
        this.erro = 'Erro ao carregar usuários';
        this.carregando = false;
      }
    });
  }

  novoUsuario(): void {
    this.usuarioEditando = undefined;
    this.exibindoFormulario = true;
    this.erro = '';
    this.sucesso = '';

    this.form = {
      nome: '',
      email: '',
      senha: '',
      perfil: 'DENTISTA'
    };
  }

  editar(usuario: Usuario): void {
    this.usuarioEditando = usuario;
    this.exibindoFormulario = true;
    this.erro = '';
    this.sucesso = '';

    this.form = {
      nome: usuario.nome,
      email: usuario.email,
      senha: '',
      perfil: usuario.perfil
    };
  }

  cancelar(): void {
    this.exibindoFormulario = false;
    this.usuarioEditando = undefined;
    this.erro = '';
  }

  salvar(): void {
    this.erro = '';
    this.sucesso = '';

    if (!this.form.nome.trim()) {
      this.erro = 'Informe o nome';
      return;
    }

    if (!this.form.email.trim()) {
      this.erro = 'Informe o email';
      return;
    }

    if (!this.emailValido(this.form.email)) {
      this.erro = 'Email inválido';
      return;
    }

    if (!this.form.perfil) {
      this.erro = 'Informe o perfil';
      return;
    }

    if (!this.usuarioEditando && !this.form.senha?.trim()) {
      this.erro = 'Informe a senha';
      return;
    }

    if (this.form.senha && this.form.senha.length < 6) {
      this.erro = 'A senha deve ter no mínimo 6 caracteres';
      return;
    }

    this.salvando = true;

    const requisicao = this.usuarioEditando
      ? this.usuarioService.atualizar(this.usuarioEditando.id, this.form)
      : this.usuarioService.cadastrar(this.form);

    requisicao.subscribe({
      next: () => {
        this.salvando = false;
        this.exibindoFormulario = false;
        this.usuarioEditando = undefined;
        this.sucesso = this.usuarioEditando
          ? 'Usuário atualizado com sucesso'
          : 'Usuário cadastrado com sucesso';
        this.carregarUsuarios();
      },
      error: () => {
        this.erro = this.usuarioEditando
          ? 'Erro ao atualizar usuário'
          : 'Erro ao cadastrar usuário';
        this.salvando = false;
      }
    });
  }

  excluir(usuario: Usuario): void {
    const confirmar = confirm(`Confirma inativar o usuário "${usuario.nome}"?`);

    if (!confirmar) {
      return;
    }

    this.usuarioService.excluir(usuario.id).subscribe({
      next: () => {
        this.sucesso = 'Usuário inativado com sucesso';
        this.carregarUsuarios();
      },
      error: () => {
        this.erro = 'Erro ao inativar usuário';
      }
    });
  }

  emailValido(email: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  }

  formatarData(data: string): string {
    return data ? new Date(data).toLocaleString('pt-BR') : '-';
  }
}