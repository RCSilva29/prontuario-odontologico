package br.com.prontuario.api.service;

import br.com.prontuario.api.dto.PacienteRequest;
import br.com.prontuario.api.entity.Paciente;
import br.com.prontuario.api.entity.Usuario;
import br.com.prontuario.api.repository.PacienteRepository;
import br.com.prontuario.api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteService {

    private final PacienteRepository repository;
    private final UsuarioRepository usuarioRepository;

    public PacienteService(
            PacienteRepository repository,
            UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Paciente> listarAtivos(String emailUsuarioLogado) {
        Usuario usuario = buscarUsuarioLogado(emailUsuarioLogado);

        if (isAdmin(usuario)) {
            return repository.findByAtivoTrue();
        }

        if (isDentista(usuario)) {
            return repository.findByAtivoTrueAndDentistaId(usuario.getId());
        }

        throw new RuntimeException("Perfil de usuário não autorizado para listar pacientes");
    }

    public Paciente cadastrar(PacienteRequest request, String emailUsuarioLogado) {
        Usuario usuario = buscarUsuarioLogado(emailUsuarioLogado);

        Paciente paciente = new Paciente();
        preencherDados(paciente, request);

        if (isDentista(usuario)) {
            paciente.setDentista(usuario);
        }

        return repository.save(paciente);
    }

    public Paciente buscarPorId(Long id, String emailUsuarioLogado) {
        Usuario usuario = buscarUsuarioLogado(emailUsuarioLogado);

        if (isAdmin(usuario)) {
            return repository.findByIdAndAtivoTrue(id)
                    .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        }

        if (isDentista(usuario)) {
            return repository.findByIdAndAtivoTrueAndDentistaId(id, usuario.getId())
                    .orElseThrow(
                            () -> new RuntimeException("Paciente não encontrado ou não vinculado ao dentista logado"));
        }

        throw new RuntimeException("Perfil de usuário não autorizado para consultar paciente");
    }

    public Paciente atualizar(Long id, PacienteRequest request, String emailUsuarioLogado) {
        Paciente paciente = buscarPorId(id, emailUsuarioLogado);
        preencherDados(paciente, request);
        return repository.save(paciente);
    }

    public void excluir(Long id, String emailUsuarioLogado) {
        Paciente paciente = buscarPorId(id, emailUsuarioLogado);
        paciente.setAtivo(false);
        repository.save(paciente);
    }

    private Usuario buscarUsuarioLogado(String emailUsuarioLogado) {
        if (emailUsuarioLogado == null || emailUsuarioLogado.isBlank()) {
            throw new RuntimeException("Usuário autenticado não identificado");
        }

        return usuarioRepository.findByEmail(emailUsuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado"));
    }

    private boolean isAdmin(Usuario usuario) {
        return "ADMIN".equals(usuario.getPerfil());
    }

    private boolean isDentista(Usuario usuario) {
        return "DENTISTA".equals(usuario.getPerfil());
    }

    private void preencherDados(Paciente paciente, PacienteRequest request) {
        paciente.setNome(request.getNome());
        paciente.setCpf(request.getCpf());
        paciente.setDataNascimento(request.getDataNascimento());
        paciente.setTelefone(request.getTelefone());
        paciente.setEmail(request.getEmail());
        paciente.setObservacoes(request.getObservacoes());
    }
}