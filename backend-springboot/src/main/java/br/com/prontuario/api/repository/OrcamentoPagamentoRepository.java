package br.com.prontuario.api.repository;

import br.com.prontuario.api.entity.OrcamentoPagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrcamentoPagamentoRepository extends JpaRepository<OrcamentoPagamento, Long> {

    List<OrcamentoPagamento> findByOrcamentoIdOrderByDataPagamentoDesc(Long orcamentoId);

    Optional<OrcamentoPagamento> findByIdAndOrcamentoId(Long id, Long orcamentoId);
}