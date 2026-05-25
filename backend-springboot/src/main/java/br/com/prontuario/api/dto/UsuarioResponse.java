package br.com.prontuario.api.dto;

import br.com.prontuario.api.entity.Usuario;

import java.time.LocalDateTime;

public class UsuarioResponse {

    private Long id;
    private String nome;
    private String email;
    private Boolean ativo;
    private LocalDateTime dataCriacao;
    private String perfil;

    public UsuarioResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.ativo = usuario.getAtivo();
        this.dataCriacao = usuario.getDataCriacao();
        this.perfil = usuario.getPerfil();
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

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public String getPerfil() {
        return perfil;
    }
}