package br.com.prontuario.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrcamentoResponse {

    private Long id;
    private Long pacienteId;
    private String pacienteNome;
    private Long usuarioId;
    private String usuarioNome;
    private LocalDateTime dataCriacao;
    private Integer validadeDias;
    private String observacoes;
    private BigDecimal subtotal;
    private BigDecimal desconto;
    private BigDecimal total;
    private String status;
    private List<OrcamentoItemResponse> itens;
    private BigDecimal totalPago;
    private BigDecimal saldoDevedor;
    private List<OrcamentoPagamentoResponse> pagamentos;

    public Long getId() {
        return id;
    }

    public OrcamentoResponse setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public OrcamentoResponse setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
        return this;
    }

    public String getPacienteNome() {
        return pacienteNome;
    }

    public OrcamentoResponse setPacienteNome(String pacienteNome) {
        this.pacienteNome = pacienteNome;
        return this;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public OrcamentoResponse setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
        return this;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }

    public OrcamentoResponse setUsuarioNome(String usuarioNome) {
        this.usuarioNome = usuarioNome;
        return this;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public OrcamentoResponse setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
        return this;
    }

    public Integer getValidadeDias() {
        return validadeDias;
    }

    public OrcamentoResponse setValidadeDias(Integer validadeDias) {
        this.validadeDias = validadeDias;
        return this;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public OrcamentoResponse setObservacoes(String observacoes) {
        this.observacoes = observacoes;
        return this;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public OrcamentoResponse setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
        return this;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public OrcamentoResponse setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
        return this;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public OrcamentoResponse setTotal(BigDecimal total) {
        this.total = total;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public OrcamentoResponse setStatus(String status) {
        this.status = status;
        return this;
    }

    public List<OrcamentoItemResponse> getItens() {
        return itens;
    }

    public OrcamentoResponse setItens(List<OrcamentoItemResponse> itens) {
        this.itens = itens;
        return this;
    }

    public BigDecimal getTotalPago() {
        return totalPago;
    }

    public OrcamentoResponse setTotalPago(BigDecimal totalPago) {
        this.totalPago = totalPago;
        return this;
    }

    public BigDecimal getSaldoDevedor() {
        return saldoDevedor;
    }

    public OrcamentoResponse setSaldoDevedor(BigDecimal saldoDevedor) {
        this.saldoDevedor = saldoDevedor;
        return this;
    }

    public List<OrcamentoPagamentoResponse> getPagamentos() {
        return pagamentos;
    }

    public OrcamentoResponse setPagamentos(List<OrcamentoPagamentoResponse> pagamentos) {
        this.pagamentos = pagamentos;
        return this;
    }
}