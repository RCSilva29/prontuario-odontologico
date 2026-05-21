package br.com.prontuario.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "anamnese")
public class Anamnese {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "paciente_id", nullable = false, unique = true)
    private Paciente paciente;

    private Boolean hipertensao = false;

    private Boolean diabetes = false;

    @Column(columnDefinition = "TEXT")
    private String alergias;

    @Column(columnDefinition = "TEXT")
    private String medicamentos;

    private Boolean fumante = false;

    private Boolean gravida = false;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    private LocalDateTime dataCriacao = LocalDateTime.now();
}