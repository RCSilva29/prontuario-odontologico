package br.com.prontuario.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.List;

public class OrcamentoRequest {

    @Min(1)
    private Integer validadeDias = 30;

    private String observacoes;

    private BigDecimal desconto = BigDecimal.ZERO;

    private String status = "ABERTO";

    @Valid
    @NotEmpty
    private List<OrcamentoItemRequest> itens;

    public Integer getValidadeDias() {
        return validadeDias;
    }

    public void setValidadeDias(Integer validadeDias) {
        this.validadeDias = validadeDias;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrcamentoItemRequest> getItens() {
        return itens;
    }

    public void setItens(List<OrcamentoItemRequest> itens) {
        this.itens = itens;
    }
}