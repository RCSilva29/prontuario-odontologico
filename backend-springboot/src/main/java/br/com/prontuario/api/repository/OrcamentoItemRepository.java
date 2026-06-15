package br.com.prontuario.api.repository;

import br.com.prontuario.api.entity.OrcamentoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrcamentoItemRepository extends JpaRepository<OrcamentoItem, Long> {
}