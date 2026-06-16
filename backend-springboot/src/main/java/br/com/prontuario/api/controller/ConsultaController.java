package br.com.prontuario.api.controller;

import br.com.prontuario.api.dto.ConsultaRequest;
import br.com.prontuario.api.dto.ConsultaResponse;
import br.com.prontuario.api.service.ConsultaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @GetMapping
    public List<ConsultaResponse> listar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return consultaService.listar(inicio, fim);
    }

    @PostMapping
    public ConsultaResponse criar(@Valid @RequestBody ConsultaRequest request) {
        return consultaService.criar(request);
    }

    @PutMapping("/{id}")
    public ConsultaResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ConsultaRequest request) {
        return consultaService.atualizar(id, request);
    }

    @PatchMapping("/{id}/cancelar")
    public void cancelar(@PathVariable Long id) {
        consultaService.cancelar(id);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        consultaService.excluir(id);
    }
}