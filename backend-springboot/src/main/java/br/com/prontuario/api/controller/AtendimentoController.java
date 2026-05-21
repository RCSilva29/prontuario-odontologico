package br.com.prontuario.api.controller;

import br.com.prontuario.api.dto.AtendimentoRequest;
import br.com.prontuario.api.dto.AtendimentoResponse;
import br.com.prontuario.api.entity.Atendimento;
import br.com.prontuario.api.service.AtendimentoService;
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
    public List<AtendimentoResponse> listar(@PathVariable Long pacienteId) {
        return service.listarPorPaciente(pacienteId)
                .stream()
                .map(AtendimentoResponse::new)
                .toList();
    }

    @PostMapping
    public AtendimentoResponse cadastrar(
            @PathVariable Long pacienteId,
            @RequestBody AtendimentoRequest request
    ) {
        Atendimento atendimento = service.cadastrar(pacienteId, request);
        return new AtendimentoResponse(atendimento);
    }

    @PutMapping("/{atendimentoId}")
    public AtendimentoResponse atualizar(
            @PathVariable Long atendimentoId,
            @RequestBody AtendimentoRequest request
    ) {
        Atendimento atendimento = service.atualizar(atendimentoId, request);
        return new AtendimentoResponse(atendimento);
    }
}