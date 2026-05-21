package br.com.prontuario.api.service;

import br.com.prontuario.api.dto.AtendimentoRequest;
import br.com.prontuario.api.entity.Atendimento;
import br.com.prontuario.api.entity.Paciente;
import br.com.prontuario.api.repository.AtendimentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AtendimentoService {

    private final AtendimentoRepository repository;
    private final PacienteService pacienteService;

    public AtendimentoService(
            AtendimentoRepository repository,
            PacienteService pacienteService
    ) {
        this.repository = repository;
        this.pacienteService = pacienteService;
    }

    public List<Atendimento> listarPorPaciente(Long pacienteId) {
        return repository.findByPacienteIdOrderByDataAtendimentoDesc(pacienteId);
    }

    public Atendimento cadastrar(Long pacienteId, AtendimentoRequest request) {
        Paciente paciente = pacienteService.buscarPorId(pacienteId);

        Atendimento atendimento = new Atendimento();
        atendimento.setPaciente(paciente);

        preencherDados(atendimento, request);

        return repository.save(atendimento);
    }

    public Atendimento buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atendimento não encontrado"));
    }

    public Atendimento atualizar(Long id, AtendimentoRequest request) {
        Atendimento atendimento = buscarPorId(id);
        preencherDados(atendimento, request);
        return repository.save(atendimento);
    }

    private void preencherDados(Atendimento atendimento, AtendimentoRequest request) {
        atendimento.setQueixaPrincipal(request.getQueixaPrincipal());
        atendimento.setEvolucaoClinica(request.getEvolucaoClinica());
        atendimento.setProcedimentoRealizado(request.getProcedimentoRealizado());
        atendimento.setObservacoes(request.getObservacoes());
    }
}