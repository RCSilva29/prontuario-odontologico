package br.com.prontuario.api.controller;

import br.com.prontuario.api.dto.PacienteRequest;
import br.com.prontuario.api.dto.PacienteResponse;
import br.com.prontuario.api.entity.Paciente;
import br.com.prontuario.api.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService service;

    public PacienteController(PacienteService service) {
        this.service = service;
    }

    @GetMapping
    public List<PacienteResponse> listar(
            @RequestParam(required = false) String termo,
            Authentication authentication) {

        return service.listarAtivos(
                termo,
                obterEmailUsuarioLogado(authentication))
                .stream()
                .map(PacienteResponse::new)
                .toList();
    }

    @PostMapping
    public PacienteResponse cadastrar(
            @Valid @RequestBody PacienteRequest request,
            Authentication authentication) {
        Paciente paciente = service.cadastrar(
                request,
                obterEmailUsuarioLogado(authentication));

        return new PacienteResponse(paciente);
    }

    @GetMapping("/{id}")
    public PacienteResponse buscarPorId(
            @PathVariable Long id,
            Authentication authentication) {
        Paciente paciente = service.buscarPorId(
                id,
                obterEmailUsuarioLogado(authentication));

        return new PacienteResponse(paciente);
    }

    @PutMapping("/{id}")
    public PacienteResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PacienteRequest request,
            Authentication authentication) {
        Paciente paciente = service.atualizar(
                id,
                request,
                obterEmailUsuarioLogado(authentication));

        return new PacienteResponse(paciente);
    }

    @DeleteMapping("/{id}")
    public void excluir(
            @PathVariable Long id,
            Authentication authentication) {
        service.excluir(
                id,
                obterEmailUsuarioLogado(authentication));
    }

    private String obterEmailUsuarioLogado(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Usuário autenticado não identificado");
        }

        return authentication.getName();
    }
}