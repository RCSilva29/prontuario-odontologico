package br.com.prontuario.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrcamentoPagamentoResponse {

    private Long id;
    private LocalDateTime dataPagamento;
    private BigDecimal valorPago;
    private String formaPagamento;
    private String observacao;

    public Long getId() {
        return id;
    }

    public OrcamentoPagamentoResponse setId(Long id) {
        this.id = id;
        return this;
    }

    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }

    public OrcamentoPagamentoResponse setDataPagamento(LocalDateTime dataPagamento) {
        this.dataPagamento = dataPagamento;
        return this;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public OrcamentoPagamentoResponse setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
        return this;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public OrcamentoPagamentoResponse setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
        return this;
    }

    public String getObservacao() {
        return observacao;
    }

    public OrcamentoPagamentoResponse setObservacao(String observacao) {
        this.observacao = observacao;
        return this;
    }
}