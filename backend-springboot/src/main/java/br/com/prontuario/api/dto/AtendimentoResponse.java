package br.com.prontuario.api.dto;

import br.com.prontuario.api.entity.Atendimento;

import java.time.LocalDateTime;

public class AtendimentoResponse {

    private Long id;
    private Long pacienteId;
    private LocalDateTime dataAtendimento;
    private String queixaPrincipal;
    private String evolucaoClinica;
    private String procedimentoRealizado;
    private String observacoes;

    public AtendimentoResponse(Atendimento atendimento) {
        this.id = atendimento.getId();
        this.pacienteId = atendimento.getPaciente().getId();
        this.dataAtendimento = atendimento.getDataAtendimento();
        this.queixaPrincipal = atendimento.getQueixaPrincipal();
        this.evolucaoClinica = atendimento.getEvolucaoClinica();
        this.procedimentoRealizado = atendimento.getProcedimentoRealizado();
        this.observacoes = atendimento.getObservacoes();
    }

    public Long getId() {
        return id;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public LocalDateTime getDataAtendimento() {
        return dataAtendimento;
    }

    public String getQueixaPrincipal() {
        return queixaPrincipal;
    }

    public String getEvolucaoClinica() {
        return evolucaoClinica;
    }

    public String getProcedimentoRealizado() {
        return procedimentoRealizado;
    }

    public String getObservacoes() {
        return observacoes;
    }
}