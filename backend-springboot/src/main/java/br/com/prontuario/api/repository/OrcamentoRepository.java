package br.com.prontuario.api.repository;

import br.com.prontuario.api.entity.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {

    List<Orcamento> findByPacienteIdOrderByDataCriacaoDesc(Long pacienteId);

    Optional<Orcamento> findByIdAndPacienteId(Long id, Long pacienteId);
}