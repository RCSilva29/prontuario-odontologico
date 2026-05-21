package br.com.prontuario.api.controller;

import br.com.prontuario.api.dto.PacienteRequest;
import br.com.prontuario.api.dto.PacienteResponse;
import br.com.prontuario.api.entity.Paciente;
import br.com.prontuario.api.service.PacienteService;
import jakarta.validation.Valid;
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
    public List<PacienteResponse> listar() {
        return service.listarAtivos()
                .stream()
                .map(PacienteResponse::new)
                .toList();
    }

    @PostMapping
    public PacienteResponse cadastrar(@Valid @RequestBody PacienteRequest request) {
        Paciente paciente = service.cadastrar(request);
        return new PacienteResponse(paciente);
    }

    @GetMapping("/{id}")
    public PacienteResponse buscarPorId(@PathVariable Long id) {
        Paciente paciente = service.buscarPorId(id);
        return new PacienteResponse(paciente);
    }

    @PutMapping("/{id}")
    public PacienteResponse atualizar(@PathVariable Long id, @Valid @RequestBody PacienteRequest request) {
        Paciente paciente = service.atualizar(id, request);
        return new PacienteResponse(paciente);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}