package br.com.prontuario.api.service;

import br.com.prontuario.api.dto.PacienteRequest;
import br.com.prontuario.api.entity.Paciente;
import br.com.prontuario.api.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteService {

    private final PacienteRepository repository;

    public PacienteService(PacienteRepository repository) {
        this.repository = repository;
    }

    public List<Paciente> listarAtivos() {
        return repository.findByAtivoTrue();
    }

    public Paciente cadastrar(PacienteRequest request) {
        Paciente paciente = new Paciente();
        preencherDados(paciente, request);
        return repository.save(paciente);
    }

    public Paciente buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
    }

    public Paciente atualizar(Long id, PacienteRequest request) {
        Paciente paciente = buscarPorId(id);
        preencherDados(paciente, request);
        return repository.save(paciente);
    }

    public void excluir(Long id) {
        Paciente paciente = buscarPorId(id);
        paciente.setAtivo(false);
        repository.save(paciente);
    }

    private void preencherDados(Paciente paciente, PacienteRequest request) {
        paciente.setNome(request.getNome());
        paciente.setCpf(request.getCpf());
        paciente.setDataNascimento(request.getDataNascimento());
        paciente.setTelefone(request.getTelefone());
        paciente.setEmail(request.getEmail());
        paciente.setObservacoes(request.getObservacoes());
    }
}