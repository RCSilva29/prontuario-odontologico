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
        return repository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public Usuario cadastrar(UsuarioRequest request) {
        Usuario usuarioExistente = repository.findByEmail(request.getEmail()).orElse(null);

        if (usuarioExistente != null) {
            if (Boolean.TRUE.equals(usuarioExistente.getAtivo())) {
                throw new RuntimeException("Já existe usuário ativo com este email");
            }

            throw new RuntimeException("Já existe usuário inativo com este email. Reative o usuário existente.");
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
        usuario.setAtivo(true);
        usuario.setBloqueado(false);
        usuario.setTentativasLogin(0);
        usuario.setTrocaSenhaObrigatoria(false);

        return repository.save(usuario);
    }

    public Usuario atualizar(Long id, UsuarioRequest request) {
        Usuario usuario = buscarPorId(id);

        repository.findByEmail(request.getEmail())
                .filter(usuarioEncontrado -> !usuarioEncontrado.getId().equals(id))
                .ifPresent(usuarioEncontrado -> {
                    if (Boolean.TRUE.equals(usuarioEncontrado.getAtivo())) {
                        throw new RuntimeException("Já existe usuário ativo com este email");
                    }

                    throw new RuntimeException(
                            "Já existe usuário inativo com este email. Reative o usuário existente.");
                });

        validarPerfil(request.getPerfil());

        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setPerfil(request.getPerfil());

        if (request.getAtivo() != null) {
            usuario.setAtivo(request.getAtivo());
        }

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

    public void excluir(Long id, String emailUsuarioLogado) {
        Usuario usuario = buscarPorId(id);

        if (usuario.getEmail().equalsIgnoreCase(emailUsuarioLogado)) {
            throw new RuntimeException("Você não pode inativar seu próprio usuário");
        }

        if ("ADMIN".equals(usuario.getPerfil())) {
            long adminsAtivos = repository.countByPerfilAndAtivoTrue("ADMIN");

            if (adminsAtivos <= 1) {
                throw new RuntimeException("Não é permitido inativar o último administrador ativo");
            }
        }

        usuario.setAtivo(false);
        usuario.setBloqueado(false);
        usuario.setTentativasLogin(0);
        usuario.setTrocaSenhaObrigatoria(false);

        repository.save(usuario);
    }

    public Usuario reativar(Long id) {
        Usuario usuario = buscarPorId(id);

        if (Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new RuntimeException("Usuário já está ativo");
        }

        usuario.setAtivo(true);
        usuario.setBloqueado(false);
        usuario.setTentativasLogin(0);
        usuario.setTrocaSenhaObrigatoria(false);

        return repository.save(usuario);
    }

    public Usuario desbloquear(Long id, String emailUsuarioLogado) {
        Usuario usuario = buscarPorId(id);

        if (usuario.getEmail().equalsIgnoreCase(emailUsuarioLogado)) {
            throw new RuntimeException("Você não pode desbloquear seu próprio usuário");
        }

        usuario.setBloqueado(false);
        usuario.setTentativasLogin(0);
        usuario.setTrocaSenhaObrigatoria(false);

        return repository.save(usuario);
    }

    public Usuario redefinirSenha(Long id, String novaSenha, String emailUsuarioLogado) {
        Usuario usuario = buscarPorId(id);

        if (usuario.getEmail().equalsIgnoreCase(emailUsuarioLogado)) {
            throw new RuntimeException("Você não pode redefinir sua própria senha por esta tela");
        }

        if (novaSenha == null || novaSenha.trim().length() < 6) {
            throw new RuntimeException("A nova senha deve ter no mínimo 6 caracteres");
        }

        if (passwordEncoder.matches(novaSenha, usuario.getSenha())) {
            throw new RuntimeException("A nova senha não pode ser igual à senha atual");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.setBloqueado(false);
        usuario.setTentativasLogin(0);
        usuario.setTrocaSenhaObrigatoria(true);
        usuario.setAtivo(true);

        return repository.save(usuario);
    }

    private void validarPerfil(String perfil) {
        if (!"ADMIN".equals(perfil) && !"DENTISTA".equals(perfil)) {
            throw new RuntimeException("Perfil inválido");
        }
    }
}