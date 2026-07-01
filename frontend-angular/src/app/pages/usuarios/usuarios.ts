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
    perfil: 'DENTISTA',
    ativo: true
  };

  erro = '';
  sucesso = '';
  carregando = false;
  pesquisando = false;
  salvando = false;
  exibindoFormulario = false;
  termoPesquisa = '';

  usuarioSenha?: Usuario;
  novaSenhaTemporaria = '';
  confirmacaoNovaSenhaTemporaria = '';
  redefinindoSenha = false;
  exibindoModalSenha = false;

  private timeoutPesquisa?: ReturnType<typeof setTimeout>;

  constructor(private usuarioService: UsuarioService) { }

  ngOnInit(): void {
    this.carregarUsuarios();
  }

  carregarUsuarios(): void {
    this.carregando = true;
    this.erro = '';

    this.usuarioService.listar(this.termoPesquisa).subscribe({
      next: (dados) => {
        this.usuarios = dados;
        this.carregando = false;
        this.pesquisando = false;
      },
      error: () => {
        this.erro = 'Erro ao carregar usuários';
        this.carregando = false;
        this.pesquisando = false;
      }
    });
  }

  pesquisarUsuarios(): void {
    this.erro = '';
    this.sucesso = '';
    this.pesquisando = true;

    if (this.timeoutPesquisa) {
      clearTimeout(this.timeoutPesquisa);
    }

    this.timeoutPesquisa = setTimeout(() => {
      this.carregarUsuarios();
    }, 350);
  }

  limparPesquisa(): void {
    this.termoPesquisa = '';
    this.carregarUsuarios();
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
      perfil: 'DENTISTA',
      ativo: true
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
      perfil: usuario.perfil,
      ativo: usuario.ativo,
      especialidade: usuario.especialidade,
      cro: usuario.cro,
      telefone: usuario.telefone
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

    if (this.form.perfil === 'DENTISTA') {
      if (!this.form.especialidade?.trim()) {
        this.erro = 'Informe a especialidade do dentista';
        return;
      }

      if (!this.form.cro?.trim()) {
        this.erro = 'Informe o CRO do dentista';
        return;
      }

      if (!this.form.telefone?.trim()) {
        this.erro = 'Informe o telefone do dentista';
        return;
      }
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

    const editando = !!this.usuarioEditando;

    const requisicao = this.usuarioEditando
      ? this.usuarioService.atualizar(this.usuarioEditando.id, this.form)
      : this.usuarioService.cadastrar(this.form);

    requisicao.subscribe({
      next: () => {
        this.salvando = false;
        this.exibindoFormulario = false;
        this.usuarioEditando = undefined;
        this.sucesso = editando
          ? 'Usuário atualizado com sucesso'
          : 'Usuário cadastrado com sucesso';
        this.carregarUsuarios();
      },
      error: (erro) => {
        this.erro =
          erro?.error?.erro ||
          erro?.error?.message ||
          (editando ? 'Erro ao atualizar usuário' : 'Erro ao cadastrar usuário');
        this.salvando = false;
      }
    });
  }

  excluir(usuario: Usuario): void {
    this.erro = '';
    this.sucesso = '';

    const confirmar = confirm(`Confirma inativar o usuário "${usuario.nome}"?`);

    if (!confirmar) {
      return;
    }

    this.usuarioService.excluir(usuario.id).subscribe({
      next: () => {
        this.erro = '';
        this.sucesso = 'Usuário inativado com sucesso';
        this.carregarUsuarios();
      },
      error: (erro) => {
        this.sucesso = '';
        this.erro = erro?.error?.erro || erro?.error?.message || 'Erro ao inativar usuário';
      }
    });
  }

  desbloquearUsuario(usuario: Usuario): void {
    const confirmar = confirm(`Deseja desbloquear o usuário "${usuario.nome}"?`);

    if (!confirmar) {
      return;
    }

    this.erro = '';
    this.sucesso = '';

    this.usuarioService.desbloquear(usuario.id).subscribe({
      next: () => {
        this.sucesso = 'Usuário desbloqueado com sucesso';
        this.carregarUsuarios();
      },
      error: (erro) => {
        this.erro = erro?.error?.erro || erro?.error?.message || 'Erro ao executar operação';
      }
    });
  }

  abrirModalRedefinirSenha(usuario: Usuario): void {
    this.erro = '';
    this.sucesso = '';
    this.usuarioSenha = usuario;
    this.novaSenhaTemporaria = '';
    this.confirmacaoNovaSenhaTemporaria = '';
    this.exibindoModalSenha = true;
  }

  cancelarRedefinicaoSenha(): void {
    this.usuarioSenha = undefined;
    this.novaSenhaTemporaria = '';
    this.confirmacaoNovaSenhaTemporaria = '';
    this.redefinindoSenha = false;
    this.exibindoModalSenha = false;
  }

  confirmarRedefinicaoSenha(): void {
    this.erro = '';
    this.sucesso = '';

    if (!this.usuarioSenha) {
      this.erro = 'Usuário não identificado para redefinição de senha';
      return;
    }

    if (!this.novaSenhaTemporaria.trim()) {
      this.erro = 'Informe a nova senha temporária';
      return;
    }

    if (this.novaSenhaTemporaria.length < 6) {
      this.erro = 'A senha deve ter no mínimo 6 caracteres';
      return;
    }

    if (this.novaSenhaTemporaria !== this.confirmacaoNovaSenhaTemporaria) {
      this.erro = 'A confirmação da senha não confere';
      return;
    }

    this.redefinindoSenha = true;

    this.usuarioService.redefinirSenha(this.usuarioSenha.id, this.novaSenhaTemporaria).subscribe({
      next: () => {
        this.sucesso = 'Senha redefinida com sucesso. O usuário deverá trocar a senha no próximo login.';
        this.cancelarRedefinicaoSenha();
        this.carregarUsuarios();
      },
      error: (erro) => {
        this.redefinindoSenha = false;
        this.erro = erro?.error?.erro || erro?.error?.message || 'Erro ao executar operação';
      }
    });
  }

  situacao(usuario: Usuario): string {
    if (!usuario.ativo) {
      return 'Inativo';
    }

    if (usuario.bloqueado) {
      return 'Bloqueado';
    }

    return 'Ativo';
  }

  emailValido(email: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  }

  formatarData(data: string): string {
    return data ? new Date(data).toLocaleString('pt-BR') : '-';
  }

  reativarUsuario(usuario: Usuario): void {
    this.erro = '';
    this.sucesso = '';

    const confirmar = confirm(`Deseja reativar o usuário "${usuario.nome}"?`);

    if (!confirmar) {
      return;
    }

    this.usuarioService.reativar(usuario.id).subscribe({
      next: () => {
        this.erro = '';
        this.sucesso = 'Usuário reativado com sucesso';
        this.carregarUsuarios();
      },
      error: (erro) => {
        this.sucesso = '';
        this.erro = erro?.error?.erro || erro?.error?.message || 'Erro ao reativar usuário';
      }
    });
  }
}