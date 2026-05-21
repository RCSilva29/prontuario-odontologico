package br.com.prontuario.api.repository;

import br.com.prontuario.api.entity.Odontograma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OdontogramaRepository extends JpaRepository<Odontograma, Long> {

    List<Odontograma> findByPacienteIdOrderByNumeroDenteAsc(Long pacienteId);
}