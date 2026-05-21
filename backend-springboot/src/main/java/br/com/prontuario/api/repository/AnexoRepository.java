package br.com.prontuario.api.repository;

import br.com.prontuario.api.entity.Anexo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnexoRepository extends JpaRepository<Anexo, Long> {

    List<Anexo> findByPacienteIdOrderByDataUploadDesc(Long pacienteId);
}