package br.com.prontuario.api.repository;

import br.com.prontuario.api.entity.Anamnese;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnamneseRepository extends JpaRepository<Anamnese, Long> {

    Optional<Anamnese> findByPacienteId(Long pacienteId);
}