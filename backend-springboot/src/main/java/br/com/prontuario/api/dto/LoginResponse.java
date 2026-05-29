package br.com.prontuario.api.dto;

public class LoginResponse {

    private Long id;
    private String nome;
    private String email;
    private String mensagem;
    private String perfil;
    private String token;
    private String especialidade;
    private String cro;

    public LoginResponse(
            Long id,
            String nome,
            String email,
            String perfil,
            String token,
            String mensagem) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.perfil = perfil;
        this.token = token;
        this.mensagem = mensagem;
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

    public String getMensagem() {
        return mensagem;
    }

    public String getPerfil() {
        return perfil;
    }

    public String getToken() {
        return token;
    }
}