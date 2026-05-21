package br.com.prontuario.api.dto;

import br.com.prontuario.api.entity.Odontograma;

import java.time.LocalDateTime;

public class OdontogramaResponse {

    private Long id;
    private Long pacienteId;
    private String numeroDente;
    private String status;
    private String observacao;
    private LocalDateTime dataRegistro;

    public OdontogramaResponse(Odontograma odontograma) {
        this.id = odontograma.getId();
        this.pacienteId = odontograma.getPaciente().getId();
        this.numeroDente = odontograma.getNumeroDente();
        this.status = odontograma.getStatus();
        this.observacao = odontograma.getObservacao();
        this.dataRegistro = odontograma.getDataRegistro();
    }

    public Long getId() { return id; }
    public Long getPacienteId() { return pacienteId; }
    public String getNumeroDente() { return numeroDente; }
    public String getStatus() { return status; }
    public String getObservacao() { return observacao; }
    public LocalDateTime getDataRegistro() { return dataRegistro; }
}