package br.com.prontuario.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "atendimento")
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    private LocalDateTime dataAtendimento = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String queixaPrincipal;

    @Column(columnDefinition = "TEXT")
    private String evolucaoClinica;

    @Column(columnDefinition = "TEXT")
    private String procedimentoRealizado;

    @Column(columnDefinition = "TEXT")
    private String observacoes;
}