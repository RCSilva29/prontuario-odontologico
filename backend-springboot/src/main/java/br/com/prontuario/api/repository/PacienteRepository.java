package br.com.prontuario.api.repository;

import br.com.prontuario.api.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    List<Paciente> findByAtivoTrue();

    List<Paciente> findByAtivoTrueAndDentistaId(Long dentistaId);

    Optional<Paciente> findByIdAndAtivoTrue(Long id);

    Optional<Paciente> findByIdAndAtivoTrueAndDentistaId(Long id, Long dentistaId);
}