package br.com.prontuario.api.service;

import br.com.prontuario.api.dto.AnamneseRequest;
import br.com.prontuario.api.entity.Anamnese;
import br.com.prontuario.api.entity.Paciente;
import br.com.prontuario.api.repository.AnamneseRepository;
import org.springframework.stereotype.Service;

@Service
public class AnamneseService {

    private final AnamneseRepository repository;
    private final PacienteService pacienteService;

    public AnamneseService(
            AnamneseRepository repository,
            PacienteService pacienteService) {
        this.repository = repository;
        this.pacienteService = pacienteService;
    }

    public Anamnese buscarPorPaciente(Long pacienteId, String emailUsuarioLogado) {
        pacienteService.buscarPorId(pacienteId, emailUsuarioLogado);

        return repository.findByPacienteId(pacienteId)
                .orElseThrow(() -> new RuntimeException("Anamnese não encontrada"));
    }

    public Anamnese cadastrar(
            Long pacienteId,
            AnamneseRequest request,
            String emailUsuarioLogado) {
        Paciente paciente = pacienteService.buscarPorId(
                pacienteId,
                emailUsuarioLogado);

        Anamnese anamnese = new Anamnese();
        anamnese.setPaciente(paciente);

        preencherDados(anamnese, request);

        return repository.save(anamnese);
    }

    public Anamnese atualizar(
            Long pacienteId,
            AnamneseRequest request,
            String emailUsuarioLogado) {
        Anamnese anamnese = buscarPorPaciente(
                pacienteId,
                emailUsuarioLogado);

        preencherDados(anamnese, request);

        return repository.save(anamnese);
    }

    private void preencherDados(Anamnese anamnese, AnamneseRequest request) {
        anamnese.setHipertensao(request.getHipertensao());
        anamnese.setDiabetes(request.getDiabetes());
        anamnese.setAlergias(request.getAlergias());
        anamnese.setMedicamentos(request.getMedicamentos());
        anamnese.setFumante(request.getFumante());
        anamnese.setGravida(request.getGravida());
        anamnese.setObservacoes(request.getObservacoes());
    }
}