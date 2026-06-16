package br.com.prontuario.api.dto;

import java.time.LocalDateTime;

public class ConsultaResponse {

    private Long id;
    private Long pacienteId;
    private String pacienteNome;
    private Long dentistaId;
    private String dentistaNome;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private String observacao;
    private String status;

    public Long getId() {
        return id;
    }

    public ConsultaResponse setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public ConsultaResponse setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
        return this;
    }

    public String getPacienteNome() {
        return pacienteNome;
    }

    public ConsultaResponse setPacienteNome(String pacienteNome) {
        this.pacienteNome = pacienteNome;
        return this;
    }

    public Long getDentistaId() {
        return dentistaId;
    }

    public ConsultaResponse setDentistaId(Long dentistaId) {
        this.dentistaId = dentistaId;
        return this;
    }

    public String getDentistaNome() {
        return dentistaNome;
    }

    public ConsultaResponse setDentistaNome(String dentistaNome) {
        this.dentistaNome = dentistaNome;
        return this;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public ConsultaResponse setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
        return this;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public ConsultaResponse setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
        return this;
    }

    public String getObservacao() {
        return observacao;
    }

    public ConsultaResponse setObservacao(String observacao) {
        this.observacao = observacao;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public ConsultaResponse setStatus(String status) {
        this.status = status;
        return this;
    }
}