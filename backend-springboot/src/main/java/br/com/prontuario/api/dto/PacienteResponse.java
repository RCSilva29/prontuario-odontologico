package br.com.prontuario.api.dto;

import br.com.prontuario.api.entity.Paciente;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PacienteResponse {

    private Long id;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private String telefone;
    private String email;
    private String observacoes;
    private Boolean ativo;
    private LocalDateTime dataCriacao;

    public PacienteResponse(Paciente paciente) {
        this.id = paciente.getId();
        this.nome = paciente.getNome();
        this.cpf = paciente.getCpf();
        this.dataNascimento = paciente.getDataNascimento();
        this.telefone = paciente.getTelefone();
        this.email = paciente.getEmail();
        this.observacoes = paciente.getObservacoes();
        this.ativo = paciente.getAtivo();
        this.dataCriacao = paciente.getDataCriacao();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}