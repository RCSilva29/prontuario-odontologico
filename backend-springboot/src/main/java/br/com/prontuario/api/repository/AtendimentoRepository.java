package br.com.prontuario.api.repository;

import br.com.prontuario.api.entity.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    List<Atendimento> findByPacienteIdOrderByDataAtendimentoDesc(Long pacienteId);
}