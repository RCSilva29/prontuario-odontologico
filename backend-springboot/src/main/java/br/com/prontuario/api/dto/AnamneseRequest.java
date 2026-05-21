package br.com.prontuario.api.dto;

public class AnamneseRequest {

    private Boolean hipertensao;
    private Boolean diabetes;
    private String alergias;
    private String medicamentos;
    private Boolean fumante;
    private Boolean gravida;
    private String observacoes;

    public Boolean getHipertensao() {
        return hipertensao;
    }

    public Boolean getDiabetes() {
        return diabetes;
    }

    public String getAlergias() {
        return alergias;
    }

    public String getMedicamentos() {
        return medicamentos;
    }

    public Boolean getFumante() {
        return fumante;
    }

    public Boolean getGravida() {
        return gravida;
    }

    public String getObservacoes() {
        return observacoes;
    }
}