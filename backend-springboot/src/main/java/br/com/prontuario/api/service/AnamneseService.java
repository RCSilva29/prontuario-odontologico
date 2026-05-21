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

    public Anamnese buscarPorPaciente(Long pacienteId) {
        return repository.findByPacienteId(pacienteId)
                .orElseThrow(() -> new RuntimeException("Anamnese não encontrada"));
    }

    public Anamnese cadastrar(Long pacienteId, AnamneseRequest request) {

        Paciente paciente = pacienteService.buscarPorId(pacienteId);

        Anamnese anamnese = new Anamnese();
        anamnese.setPaciente(paciente);

        preencherDados(anamnese, request);

        return repository.save(anamnese);
    }

    public Anamnese atualizar(Long pacienteId, AnamneseRequest request) {

        Anamnese anamnese = buscarPorPaciente(pacienteId);

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