package br.com.prontuario.api.repository;

import br.com.prontuario.api.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    List<Paciente> findByAtivoTrue();
}