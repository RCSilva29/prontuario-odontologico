package br.com.prontuario.api.dto;

import java.math.BigDecimal;

public class OrcamentoItemResponse {

    private Long id;
    private String codigo;
    private String dentes;
    private String procedimento;
    private Integer quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal subtotal;

    public Long getId() {
        return id;
    }

    public OrcamentoItemResponse setId(Long id) {
        this.id = id;
        return this;
    }

    public String getCodigo() {
        return codigo;
    }

    public OrcamentoItemResponse setCodigo(String codigo) {
        this.codigo = codigo;
        return this;
    }

    public String getDentes() {
        return dentes;
    }

    public OrcamentoItemResponse setDentes(String dentes) {
        this.dentes = dentes;
        return this;
    }

    public String getProcedimento() {
        return procedimento;
    }

    public OrcamentoItemResponse setProcedimento(String procedimento) {
        this.procedimento = procedimento;
        return this;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public OrcamentoItemResponse setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
        return this;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public OrcamentoItemResponse setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
        return this;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public OrcamentoItemResponse setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
        return this;
    }
}