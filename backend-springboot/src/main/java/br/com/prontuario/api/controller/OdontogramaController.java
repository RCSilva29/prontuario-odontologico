package br.com.prontuario.api.controller;

import br.com.prontuario.api.dto.OdontogramaRequest;
import br.com.prontuario.api.dto.OdontogramaResponse;
import br.com.prontuario.api.entity.Odontograma;
import br.com.prontuario.api.service.OdontogramaService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pacientes/{pacienteId}/odontograma")
public class OdontogramaController {

    private final OdontogramaService service;

    public OdontogramaController(OdontogramaService service) {
        this.service = service;
    }

    @GetMapping
    public List<OdontogramaResponse> listar(
            @PathVariable Long pacienteId,
            Authentication authentication) {
        return service.listarPorPaciente(
                pacienteId,
                obterEmailUsuarioLogado(authentication))
                .stream()
                .map(OdontogramaResponse::new)
                .toList();
    }

    @PostMapping
    public OdontogramaResponse cadastrar(
            @PathVariable Long pacienteId,
            @Valid @RequestBody OdontogramaRequest request,
            Authentication authentication) {
        Odontograma odontograma = service.cadastrar(
                pacienteId,
                request,
                obterEmailUsuarioLogado(authentication));

        return new OdontogramaResponse(odontograma);
    }

    @PutMapping("/{odontogramaId}")
    public OdontogramaResponse atualizar(
            @PathVariable Long pacienteId,
            @PathVariable Long odontogramaId,
            @Valid @RequestBody OdontogramaRequest request,
            Authentication authentication) {
        Odontograma odontograma = service.atualizar(
                pacienteId,
                odontogramaId,
                request,
                obterEmailUsuarioLogado(authentication));

        return new OdontogramaResponse(odontograma);
    }

    @DeleteMapping("/{odontogramaId}")
    public void excluir(
            @PathVariable Long pacienteId,
            @PathVariable Long odontogramaId,
            Authentication authentication) {
        service.excluir(
                pacienteId,
                odontogramaId,
                obterEmailUsuarioLogado(authentication));
    }

    private String obterEmailUsuarioLogado(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Usuário autenticado não identificado");
        }

        return authentication.getName();
    }
}