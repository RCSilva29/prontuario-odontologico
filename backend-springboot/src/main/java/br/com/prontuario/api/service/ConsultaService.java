package br.com.prontuario.api.service;

import br.com.prontuario.api.dto.ConsultaRequest;
import br.com.prontuario.api.dto.ConsultaResponse;
import br.com.prontuario.api.entity.Consulta;
import br.com.prontuario.api.entity.Paciente;
import br.com.prontuario.api.entity.Usuario;
import br.com.prontuario.api.repository.ConsultaRepository;
import br.com.prontuario.api.repository.PacienteRepository;
import br.com.prontuario.api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsultaService {

    private static final String STATUS_AGENDADA = "AGENDADA";
    private static final String STATUS_CANCELADA = "CANCELADA";

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;

    public ConsultaService(
            ConsultaRepository consultaRepository,
            PacienteRepository pacienteRepository,
            UsuarioRepository usuarioRepository) {
        this.consultaRepository = consultaRepository;
        this.pacienteRepository = pacienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<ConsultaResponse> listar(LocalDateTime inicio, LocalDateTime fim) {
        return consultaRepository.findByDataHoraInicioBetweenOrderByDataHoraInicioAsc(inicio, fim)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ConsultaResponse criar(ConsultaRequest request) {
        validarRequest(request, null);

        Paciente paciente = pacienteRepository.findByIdAndAtivoTrue(request.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        Usuario dentista = usuarioRepository.findById(request.getDentistaId())
                .orElseThrow(() -> new RuntimeException("Dentista não encontrado"));

        validarDentistaDoPaciente(paciente, dentista);

        Consulta consulta = new Consulta();
        consulta.setPaciente(paciente);
        consulta.setDentista(dentista);
        consulta.setDataHoraInicio(request.getDataHoraInicio());
        consulta.setDataHoraFim(request.getDataHoraFim());
        consulta.setObservacao(request.getObservacao());
        consulta.setStatus(STATUS_AGENDADA);

        return toResponse(consultaRepository.save(consulta));
    }

    @Transactional
    public ConsultaResponse atualizar(Long id, ConsultaRequest request) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        validarRequest(request, id);

        Paciente paciente = pacienteRepository.findByIdAndAtivoTrue(request.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        Usuario dentista = usuarioRepository.findById(request.getDentistaId())
                .orElseThrow(() -> new RuntimeException("Dentista não encontrado"));

        validarDentistaDoPaciente(paciente, dentista);

        consulta.setPaciente(paciente);
        consulta.setDentista(dentista);
        consulta.setDataHoraInicio(request.getDataHoraInicio());
        consulta.setDataHoraFim(request.getDataHoraFim());
        consulta.setObservacao(request.getObservacao());

        return toResponse(consultaRepository.save(consulta));
    }

    @Transactional
    public void cancelar(Long id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        consulta.setStatus(STATUS_CANCELADA);
        consultaRepository.save(consulta);
    }

    @Transactional
    public void excluir(Long id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        consultaRepository.delete(consulta);
    }

    private void validarRequest(ConsultaRequest request, Long ignorarId) {
        if (request == null) {
            throw new RuntimeException("Dados da consulta são obrigatórios");
        }

        if (request.getPacienteId() == null) {
            throw new RuntimeException("Paciente é obrigatório");
        }

        if (request.getDentistaId() == null) {
            throw new RuntimeException("Dentista é obrigatório");
        }

        if (request.getDataHoraInicio() == null) {
            throw new RuntimeException("Data/hora de início é obrigatória");
        }

        if (request.getDataHoraFim() == null) {
            throw new RuntimeException("Data/hora de fim é obrigatória");
        }

        if (!request.getDataHoraFim().isAfter(request.getDataHoraInicio())) {
            throw new RuntimeException("Horário final deve ser posterior ao horário inicial");
        }

        List<Consulta> conflitos = consultaRepository.buscarConflitos(
                request.getDataHoraInicio(),
                request.getDataHoraFim(),
                ignorarId);

        if (!conflitos.isEmpty()) {
            Consulta conflito = conflitos.get(0);

            throw new RuntimeException(
                    "Já existe consulta agendada neste horário para o paciente "
                            + conflito.getPaciente().getNome()
                            + " com "
                            + conflito.getDentista().getNome());
        }
    }

    private void validarDentistaDoPaciente(Paciente paciente, Usuario dentista) {
        if (paciente.getDentista() == null) {
            throw new RuntimeException("Este paciente não possui dentista responsável vinculado");
        }

        if (!paciente.getDentista().getId().equals(dentista.getId())) {
            throw new RuntimeException(
                    "Este paciente está vinculado ao dentista "
                            + paciente.getDentista().getNome()
                            + ". Selecione o dentista responsável correto.");
        }
    }

    private ConsultaResponse toResponse(Consulta consulta) {
        return new ConsultaResponse()
                .setId(consulta.getId())
                .setPacienteId(consulta.getPaciente().getId())
                .setPacienteNome(consulta.getPaciente().getNome())
                .setDentistaId(consulta.getDentista().getId())
                .setDentistaNome(consulta.getDentista().getNome())
                .setDataHoraInicio(consulta.getDataHoraInicio())
                .setDataHoraFim(consulta.getDataHoraFim())
                .setObservacao(consulta.getObservacao())
                .setStatus(consulta.getStatus());
    }
}