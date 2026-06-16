package br.com.prontuario.api.repository;

import br.com.prontuario.api.entity.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    List<Consulta> findByDataHoraInicioBetweenOrderByDataHoraInicioAsc(
            LocalDateTime inicio,
            LocalDateTime fim);

    @Query("""
            select c
            from Consulta c
            where c.status = 'AGENDADA'
              and (:ignorarId is null or c.id <> :ignorarId)
              and c.dataHoraInicio < :fim
              and c.dataHoraFim > :inicio
            """)
    List<Consulta> buscarConflitos(
            LocalDateTime inicio,
            LocalDateTime fim,
            Long ignorarId);
}