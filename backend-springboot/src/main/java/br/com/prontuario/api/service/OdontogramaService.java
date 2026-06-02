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

    public OdontogramaService(
            OdontogramaRepository repository,
            PacienteService pacienteService) {
        this.repository = repository;
        this.pacienteService = pacienteService;
    }

    public List<Odontograma> listarPorPaciente(
            Long pacienteId,
            String emailUsuarioLogado) {
        pacienteService.buscarPorId(
                pacienteId,
                emailUsuarioLogado);

        return repository.findByPacienteIdOrderByNumeroDenteAsc(pacienteId);
    }

    public Odontograma cadastrar(
            Long pacienteId,
            OdontogramaRequest request,
            String emailUsuarioLogado) {
        Paciente paciente = pacienteService.buscarPorId(
                pacienteId,
                emailUsuarioLogado);

        Odontograma odontograma = new Odontograma();
        odontograma.setPaciente(paciente);

        preencherDados(odontograma, request);

        return repository.save(odontograma);
    }

    public Odontograma buscarPorId(
            Long pacienteId,
            Long odontogramaId,
            String emailUsuarioLogado) {
        pacienteService.buscarPorId(
                pacienteId,
                emailUsuarioLogado);

        Odontograma odontograma = repository.findById(odontogramaId)
                .orElseThrow(() -> new RuntimeException("Registro do odontograma não encontrado"));

        if (odontograma.getPaciente() == null
                || !pacienteId.equals(odontograma.getPaciente().getId())) {
            throw new RuntimeException(
                    "Registro do odontograma não pertence ao paciente informado");
        }

        return odontograma;
    }

    public Odontograma atualizar(
            Long pacienteId,
            Long odontogramaId,
            OdontogramaRequest request,
            String emailUsuarioLogado) {
        Odontograma odontograma = buscarPorId(
                pacienteId,
                odontogramaId,
                emailUsuarioLogado);

        preencherDados(odontograma, request);

        return repository.save(odontograma);
    }

    public void excluir(
            Long pacienteId,
            Long odontogramaId,
            String emailUsuarioLogado) {
        Odontograma odontograma = buscarPorId(
                pacienteId,
                odontogramaId,
                emailUsuarioLogado);

        repository.delete(odontograma);
    }

    private void preencherDados(
            Odontograma odontograma,
            OdontogramaRequest request) {
        odontograma.setNumeroDente(request.getNumeroDente());
        odontograma.setStatus(request.getStatus());
        odontograma.setObservacao(request.getObservacao());
    }
}