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
            PacienteService pacienteService) {
        this.repository = repository;
        this.pacienteService = pacienteService;
    }

    public List<Atendimento> listarPorPaciente(
            Long pacienteId,
            String emailUsuarioLogado) {
        pacienteService.buscarPorId(pacienteId, emailUsuarioLogado);
        return repository.findByPacienteIdOrderByDataAtendimentoDesc(pacienteId);
    }

    public Atendimento cadastrar(
            Long pacienteId,
            AtendimentoRequest request,
            String emailUsuarioLogado) {
        Paciente paciente = pacienteService.buscarPorId(
                pacienteId,
                emailUsuarioLogado);

        Atendimento atendimento = new Atendimento();
        atendimento.setPaciente(paciente);

        preencherDados(atendimento, request);

        return repository.save(atendimento);
    }

    public Atendimento buscarPorId(
            Long pacienteId,
            Long atendimentoId,
            String emailUsuarioLogado) {
        pacienteService.buscarPorId(pacienteId, emailUsuarioLogado);

        Atendimento atendimento = repository.findById(atendimentoId)
                .orElseThrow(() -> new RuntimeException("Atendimento não encontrado"));

        if (atendimento.getPaciente() == null || !pacienteId.equals(atendimento.getPaciente().getId())) {
            throw new RuntimeException("Atendimento não pertence ao paciente informado");
        }

        return atendimento;
    }

    public Atendimento atualizar(
            Long pacienteId,
            Long atendimentoId,
            AtendimentoRequest request,
            String emailUsuarioLogado) {
        Atendimento atendimento = buscarPorId(
                pacienteId,
                atendimentoId,
                emailUsuarioLogado);

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