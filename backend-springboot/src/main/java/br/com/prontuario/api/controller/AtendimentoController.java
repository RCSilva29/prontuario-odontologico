package br.com.prontuario.api.controller;

import br.com.prontuario.api.dto.AtendimentoRequest;
import br.com.prontuario.api.dto.AtendimentoResponse;
import br.com.prontuario.api.entity.Atendimento;
import br.com.prontuario.api.service.AtendimentoService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pacientes/{pacienteId}/atendimentos")
public class AtendimentoController {

    private final AtendimentoService service;

    public AtendimentoController(AtendimentoService service) {
        this.service = service;
    }

    @GetMapping
    public List<AtendimentoResponse> listar(
            @PathVariable Long pacienteId,
            Authentication authentication) {
        return service.listarPorPaciente(
                pacienteId,
                obterEmailUsuarioLogado(authentication))
                .stream()
                .map(AtendimentoResponse::new)
                .toList();
    }

    @PostMapping
    public AtendimentoResponse cadastrar(
            @PathVariable Long pacienteId,
            @RequestBody AtendimentoRequest request,
            Authentication authentication) {
        Atendimento atendimento = service.cadastrar(
                pacienteId,
                request,
                obterEmailUsuarioLogado(authentication));

        return new AtendimentoResponse(atendimento);
    }

    @PutMapping("/{atendimentoId}")
    public AtendimentoResponse atualizar(
            @PathVariable Long pacienteId,
            @PathVariable Long atendimentoId,
            @RequestBody AtendimentoRequest request,
            Authentication authentication) {
        Atendimento atendimento = service.atualizar(
                pacienteId,
                atendimentoId,
                request,
                obterEmailUsuarioLogado(authentication));

        return new AtendimentoResponse(atendimento);
    }

    private String obterEmailUsuarioLogado(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Usuário autenticado não identificado");
        }

        return authentication.getName();
    }
}