package br.com.prontuario.api.service;

import br.com.prontuario.api.dto.AlterarSenhaRequest;
import br.com.prontuario.api.dto.UsuarioRequest;
import br.com.prontuario.api.entity.Usuario;
import br.com.prontuario.api.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository repository,
            BCryptPasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listarAtivos() {
        return repository.findByAtivoTrue();
    }

    public Usuario buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public Usuario cadastrar(UsuarioRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Já existe usuário cadastrado com este email");
        }

        if (request.getSenha() == null || request.getSenha().isBlank()) {
            throw new RuntimeException("Senha é obrigatória");
        }

        validarPerfil(request.getPerfil());

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setPerfil(request.getPerfil());

        return repository.save(usuario);
    }

    public Usuario atualizar(Long id, UsuarioRequest request) {
        Usuario usuario = buscarPorId(id);

        repository.findByEmail(request.getEmail())
                .filter(usuarioEncontrado -> !usuarioEncontrado.getId().equals(id))
                .ifPresent(usuarioEncontrado -> {
                    throw new RuntimeException("Já existe usuário cadastrado com este email");
                });

        validarPerfil(request.getPerfil());

        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setPerfil(request.getPerfil());

        if (request.getSenha() != null && !request.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        }

        return repository.save(usuario);
    }

    public void alterarSenha(Long id, AlterarSenhaRequest request) {
        Usuario usuario = buscarPorId(id);

        if (!passwordEncoder.matches(request.getSenhaAtual(), usuario.getSenha())) {
            throw new RuntimeException("Senha atual inválida");
        }

        if (passwordEncoder.matches(request.getNovaSenha(), usuario.getSenha())) {
            throw new RuntimeException("A nova senha não pode ser igual à senha atual");
        }

        usuario.setSenha(passwordEncoder.encode(request.getNovaSenha()));
        repository.save(usuario);
    }

    public void excluir(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(false);
        repository.save(usuario);
    }

    private void validarPerfil(String perfil) {
        if (!"ADMIN".equals(perfil) && !"DENTISTA".equals(perfil)) {
            throw new RuntimeException("Perfil inválido");
        }
    }
}