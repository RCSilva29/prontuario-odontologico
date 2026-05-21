package br.com.prontuario.api.dto;

public class AtendimentoRequest {

    private String queixaPrincipal;
    private String evolucaoClinica;
    private String procedimentoRealizado;
    private String observacoes;

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