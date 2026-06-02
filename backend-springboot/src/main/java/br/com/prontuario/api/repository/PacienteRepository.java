package br.com.prontuario.api.repository;

import br.com.prontuario.api.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    List<Paciente> findByAtivoTrue();

    List<Paciente> findByAtivoTrueAndDentistaId(Long dentistaId);

    List<Paciente> findByAtivoTrueAndNomeContainingIgnoreCaseOrAtivoTrueAndCpfContainingIgnoreCase(
            String nome,
            String cpf);

    List<Paciente> findByAtivoTrueAndDentistaIdAndNomeContainingIgnoreCaseOrAtivoTrueAndDentistaIdAndCpfContainingIgnoreCase(
            Long dentistaIdNome,
            String nome,
            Long dentistaIdCpf,
            String cpf);

    Optional<Paciente> findByIdAndAtivoTrue(Long id);

    Optional<Paciente> findByIdAndAtivoTrueAndDentistaId(Long id, Long dentistaId);
}