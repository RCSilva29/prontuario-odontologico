package br.com.prontuario.api.controller;

import br.com.prontuario.api.dto.AlterarSenhaRequest;
import br.com.prontuario.api.dto.UsuarioRequest;
import br.com.prontuario.api.dto.UsuarioResponse;
import br.com.prontuario.api.entity.Usuario;
import br.com.prontuario.api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import br.com.prontuario.api.dto.RedefinirSenhaRequest;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return service.listarAtivos()
                .stream()
                .map(UsuarioResponse::new)
                .toList();
    }

    @PostMapping
    public UsuarioResponse cadastrar(@Valid @RequestBody UsuarioRequest request) {
        Usuario usuario = service.cadastrar(request);
        return new UsuarioResponse(usuario);
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscarPorId(@PathVariable Long id) {
        Usuario usuario = service.buscarPorId(id);
        return new UsuarioResponse(usuario);
    }

    @DeleteMapping("/{id}")
    public void excluir(
            @PathVariable Long id,
            Authentication authentication) {
        service.excluir(id, authentication.getName());
    }

    @PutMapping("/{id}/senha")
    public void alterarSenha(
            @PathVariable Long id,
            @Valid @RequestBody AlterarSenhaRequest request) {
        service.alterarSenha(id, request);
    }

    @PutMapping("/{id}")
    public UsuarioResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequest request) {
        Usuario usuario = service.atualizar(id, request);
        return new UsuarioResponse(usuario);
    }

    @PutMapping("/{id}/desbloquear")
    public Usuario desbloquear(
            @PathVariable Long id,
            Authentication authentication) {
        return service.desbloquear(id, authentication.getName());
    }

    @PutMapping("/{id}/redefinir-senha")
    public Usuario redefinirSenha(
            @PathVariable Long id,
            @RequestBody RedefinirSenhaRequest request,
            Authentication authentication) {
        return service.redefinirSenha(id, request.getNovaSenha(), authentication.getName());
    }

    @PutMapping("/{id}/reativar")
    public UsuarioResponse reativar(@PathVariable Long id) {
        return new UsuarioResponse(service.reativar(id));
    }
}