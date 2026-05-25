package br.com.prontuario.api.service;

import br.com.prontuario.api.dto.LoginRequest;
import br.com.prontuario.api.dto.LoginResponse;
import br.com.prontuario.api.dto.TrocaSenhaObrigatoriaRequest;
import br.com.prontuario.api.entity.Usuario;
import br.com.prontuario.api.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import br.com.prontuario.api.security.JwtService;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            BCryptPasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email ou senha inválidos"));

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new RuntimeException("Usuário inativo");
        }

        if (Boolean.TRUE.equals(usuario.getTrocaSenhaObrigatoria())) {
            throw new RuntimeException("TROCA_SENHA_OBRIGATORIA");
        }

        boolean senhaValida = passwordEncoder.matches(request.getSenha(), usuario.getSenha());

        if (!senhaValida) {
            int tentativas = usuario.getTentativasLogin() == null ? 0 : usuario.getTentativasLogin();
            tentativas++;

            usuario.setTentativasLogin(tentativas);

            if (tentativas >= 3) {
                usuario.setTrocaSenhaObrigatoria(true);
            }

            usuarioRepository.save(usuario);

            if (tentativas >= 3) {
                throw new RuntimeException("TROCA_SENHA_OBRIGATORIA");
            }

            throw new RuntimeException("Email ou senha inválidos");
        }

        usuario.setTentativasLogin(0);
        usuarioRepository.save(usuario);

        String token = jwtService.gerarToken(usuario);

        return new LoginResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil(),
                token,
                "Login realizado com sucesso");
    }

    public void trocarSenhaObrigatoria(TrocaSenhaObrigatoriaRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!Boolean.TRUE.equals(usuario.getTrocaSenhaObrigatoria())) {
            throw new RuntimeException("Usuário não está bloqueado para troca de senha");
        }

        if (passwordEncoder.matches(request.getNovaSenha(), usuario.getSenha())) {
            throw new RuntimeException("A nova senha não pode ser igual à senha atual");
        }

        usuario.setSenha(passwordEncoder.encode(request.getNovaSenha()));
        usuario.setTentativasLogin(0);
        usuario.setTrocaSenhaObrigatoria(false);

        usuarioRepository.save(usuario);
    }
}