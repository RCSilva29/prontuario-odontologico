package br.com.prontuario.api.repository;

import br.com.prontuario.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findByAtivoTrue();

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);
}