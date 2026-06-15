package br.com.prontuario.api.controller;

import br.com.prontuario.api.dto.OrcamentoPagamentoRequest;
import br.com.prontuario.api.dto.OrcamentoRequest;
import br.com.prontuario.api.dto.OrcamentoResponse;
import br.com.prontuario.api.service.OrcamentoService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orcamentos")
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    public OrcamentoController(OrcamentoService orcamentoService) {
        this.orcamentoService = orcamentoService;
    }

    @GetMapping("/pacientes/{pacienteId}")
    public List<OrcamentoResponse> listarPorPaciente(@PathVariable Long pacienteId) {
        return orcamentoService.listarPorPaciente(pacienteId);
    }

    @GetMapping("/{id}")
    public OrcamentoResponse buscarPorId(@PathVariable Long id) {
        return orcamentoService.buscarPorId(id);
    }

    @PostMapping("/pacientes/{pacienteId}")
    public OrcamentoResponse criar(
            @PathVariable Long pacienteId,
            @Valid @RequestBody OrcamentoRequest request,
            Authentication authentication) {
        return orcamentoService.criar(pacienteId, request, authentication.getName());
    }

    @PutMapping("/{id}")
    public OrcamentoResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody OrcamentoRequest request) {
        return orcamentoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        orcamentoService.excluir(id);
    }

    @PostMapping("/{orcamentoId}/pagamentos")
    public void registrarPagamento(
            @PathVariable Long orcamentoId,
            @Valid @RequestBody OrcamentoPagamentoRequest request) {
        orcamentoService.registrarPagamento(orcamentoId, request);
    }

    @DeleteMapping("/{orcamentoId}/pagamentos/{pagamentoId}")
    public void excluirPagamento(
            @PathVariable Long orcamentoId,
            @PathVariable Long pagamentoId) {
        orcamentoService.excluirPagamento(orcamentoId, pagamentoId);
    }
}