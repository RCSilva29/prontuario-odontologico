package br.com.prontuario.api.dto;

import br.com.prontuario.api.entity.Anamnese;

public class AnamneseResponse {

    private Long id;
    private Boolean hipertensao;
    private Boolean diabetes;
    private String alergias;
    private String medicamentos;
    private Boolean fumante;
    private Boolean gravida;
    private String observacoes;

    public AnamneseResponse(Anamnese anamnese) {
        this.id = anamnese.getId();
        this.hipertensao = anamnese.getHipertensao();
        this.diabetes = anamnese.getDiabetes();
        this.alergias = anamnese.getAlergias();
        this.medicamentos = anamnese.getMedicamentos();
        this.fumante = anamnese.getFumante();
        this.gravida = anamnese.getGravida();
        this.observacoes = anamnese.getObservacoes();
    }

    public Long getId() {
        return id;
    }

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