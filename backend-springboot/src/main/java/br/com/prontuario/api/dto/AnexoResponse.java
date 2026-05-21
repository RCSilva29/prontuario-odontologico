package br.com.prontuario.api.dto;

import br.com.prontuario.api.entity.Anexo;

import java.time.LocalDateTime;

public class AnexoResponse {

    private Long id;
    private Long pacienteId;
    private String nomeOriginal;
    private String tipoConteudo;
    private Long tamanho;
    private LocalDateTime dataUpload;

    public AnexoResponse(Anexo anexo) {
        this.id = anexo.getId();
        this.pacienteId = anexo.getPaciente().getId();
        this.nomeOriginal = anexo.getNomeOriginal();
        this.tipoConteudo = anexo.getTipoConteudo();
        this.tamanho = anexo.getTamanho();
        this.dataUpload = anexo.getDataUpload();
    }

    public Long getId() { return id; }
    public Long getPacienteId() { return pacienteId; }
    public String getNomeOriginal() { return nomeOriginal; }
    public String getTipoConteudo() { return tipoConteudo; }
    public Long getTamanho() { return tamanho; }
    public LocalDateTime getDataUpload() { return dataUpload; }
}