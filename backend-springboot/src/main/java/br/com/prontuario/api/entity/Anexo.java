package br.com.prontuario.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "anexo")
public class Anexo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    private String nomeOriginal;
    private String nomeArquivo;
    private String tipoConteudo;
    private Long tamanho;
    private String caminhoArquivo;

    private LocalDateTime dataUpload = LocalDateTime.now();
}