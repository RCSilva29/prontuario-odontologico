package br.com.prontuario.api.dto;

public class ProntuarioPdfRequest {

    private Boolean incluirAnamnese;
    private Boolean incluirOdontograma;
    private Boolean incluirAtendimentos;

    public Boolean getIncluirAnamnese() {
        return incluirAnamnese;
    }

    public void setIncluirAnamnese(Boolean incluirAnamnese) {
        this.incluirAnamnese = incluirAnamnese;
    }

    public Boolean getIncluirOdontograma() {
        return incluirOdontograma;
    }

    public void setIncluirOdontograma(Boolean incluirOdontograma) {
        this.incluirOdontograma = incluirOdontograma;
    }

    public Boolean getIncluirAtendimentos() {
        return incluirAtendimentos;
    }

    public void setIncluirAtendimentos(Boolean incluirAtendimentos) {
        this.incluirAtendimentos = incluirAtendimentos;
    }
}