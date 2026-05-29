package br.com.prontuario.api.dto;

import br.com.prontuario.api.entity.Usuario;

import java.time.LocalDateTime;

public class UsuarioResponse {

    private Long id;
    private String nome;
    private String email;
    private Boolean ativo;
    private Boolean bloqueado;
    private Integer tentativasLogin;
    private Boolean trocaSenhaObrigatoria;
    private LocalDateTime dataCriacao;
    private String perfil;
    private String especialidade;

    public String getEspecialidade() {
        return especialidade;
    }

    public String getCro() {
        return cro;
    }

    private String cro;

    public UsuarioResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.ativo = usuario.getAtivo();
        this.bloqueado = usuario.getBloqueado();
        this.tentativasLogin = usuario.getTentativasLogin();
        this.trocaSenhaObrigatoria = usuario.getTrocaSenhaObrigatoria();
        this.dataCriacao = usuario.getDataCriacao();
        this.perfil = usuario.getPerfil();
        this.especialidade = usuario.getEspecialidade();
        this.cro = usuario.getCro();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public Boolean getBloqueado() {
        return bloqueado;
    }

    public Integer getTentativasLogin() {
        return tentativasLogin;
    }

    public Boolean getTrocaSenhaObrigatoria() {
        return trocaSenhaObrigatoria;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public String getPerfil() {
        return perfil;
    }
}