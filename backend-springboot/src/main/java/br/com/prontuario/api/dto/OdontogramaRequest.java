package br.com.prontuario.api.dto;

import jakarta.validation.constraints.NotBlank;

public class OdontogramaRequest {

    @NotBlank(message = "Número do dente é obrigatório")
    private String numeroDente;

    @NotBlank(message = "Status do dente é obrigatório")
    private String status;

    private String observacao;

    public String getNumeroDente() {
        return numeroDente;
    }

    public String getStatus() {
        return status;
    }

    public String getObservacao() {
        return observacao;
    }
}