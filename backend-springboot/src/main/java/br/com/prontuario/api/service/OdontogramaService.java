package br.com.prontuario.api.service;

import br.com.prontuario.api.dto.OdontogramaRequest;
import br.com.prontuario.api.entity.Odontograma;
import br.com.prontuario.api.entity.Paciente;
import br.com.prontuario.api.repository.OdontogramaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OdontogramaService {

    private final OdontogramaRepository repository;
    private final PacienteService pacienteService;

    public OdontogramaService(OdontogramaRepository repository, PacienteService pacienteService) {
        this.repository = repository;
        this.pacienteService = pacienteService;
    }

    public List<Odontograma> listarPorPaciente(Long pacienteId) {
        return repository.findByPacienteIdOrderByNumeroDenteAsc(pacienteId);
    }

    public Odontograma cadastrar(Long pacienteId, OdontogramaRequest request) {
        Paciente paciente = pacienteService.buscarPorId(pacienteId);

        Odontograma odontograma = new Odontograma();
        odontograma.setPaciente(paciente);
        preencherDados(odontograma, request);

        return repository.save(odontograma);
    }

    public Odontograma atualizar(Long id, OdontogramaRequest request) {
        Odontograma odontograma = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro do odontograma não encontrado"));

        preencherDados(odontograma, request);

        return repository.save(odontograma);
    }

    public void excluir(Long id) {
        Odontograma odontograma = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro do odontograma não encontrado"));

        repository.delete(odontograma);
    }

    private void preencherDados(Odontograma odontograma, OdontogramaRequest request) {
        odontograma.setNumeroDente(request.getNumeroDente());
        odontograma.setStatus(request.getStatus());
        odontograma.setObservacao(request.getObservacao());
    }
}