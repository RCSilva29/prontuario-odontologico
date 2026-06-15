package br.com.prontuario.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "orcamento_pagamento")
public class OrcamentoPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orcamento_id", nullable = false)
    private Orcamento orcamento;

    @Column(nullable = false)
    private LocalDateTime dataPagamento;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valorPago;

    @Column(length = 50)
    private String formaPagamento;

    @Column(columnDefinition = "TEXT")
    private String observacao;
}