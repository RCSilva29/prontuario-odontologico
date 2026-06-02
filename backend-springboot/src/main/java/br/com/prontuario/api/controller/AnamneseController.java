package br.com.prontuario.api.controller;

import br.com.prontuario.api.dto.AnamneseRequest;
import br.com.prontuario.api.dto.AnamneseResponse;
import br.com.prontuario.api.entity.Anamnese;
import br.com.prontuario.api.service.AnamneseService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pacientes/{pacienteId}/anamnese")
public class AnamneseController {

    private final AnamneseService service;

    public AnamneseController(AnamneseService service) {
        this.service = service;
    }

    @GetMapping
    public AnamneseResponse buscar(
            @PathVariable Long pacienteId,
            Authentication authentication) {
        Anamnese anamnese = service.buscarPorPaciente(
                pacienteId,
                obterEmailUsuarioLogado(authentication));

        return new AnamneseResponse(anamnese);
    }

    @PostMapping
    public AnamneseResponse cadastrar(
            @PathVariable Long pacienteId,
            @RequestBody AnamneseRequest request,
            Authentication authentication) {
        Anamnese anamnese = service.cadastrar(
                pacienteId,
                request,
                obterEmailUsuarioLogado(authentication));

        return new AnamneseResponse(anamnese);
    }

    @PutMapping
    public AnamneseResponse atualizar(
            @PathVariable Long pacienteId,
            @RequestBody AnamneseRequest request,
            Authentication authentication) {
        Anamnese anamnese = service.atualizar(
                pacienteId,
                request,
                obterEmailUsuarioLogado(authentication));

        return new AnamneseResponse(anamnese);
    }

    private String obterEmailUsuarioLogado(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Usuário autenticado não identificado");
        }

        return authentication.getName();
    }
}