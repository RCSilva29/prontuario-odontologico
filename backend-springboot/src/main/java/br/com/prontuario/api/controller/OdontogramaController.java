package br.com.prontuario.api.controller;

import br.com.prontuario.api.dto.OdontogramaRequest;
import br.com.prontuario.api.dto.OdontogramaResponse;
import br.com.prontuario.api.entity.Odontograma;
import br.com.prontuario.api.service.OdontogramaService;
import jakarta.validation.Valid;
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
    public List<OdontogramaResponse> listar(@PathVariable Long pacienteId) {
        return service.listarPorPaciente(pacienteId)
                .stream()
                .map(OdontogramaResponse::new)
                .toList();
    }

    @PostMapping
    public OdontogramaResponse cadastrar(
            @PathVariable Long pacienteId,
            @Valid @RequestBody OdontogramaRequest request) {
        Odontograma odontograma = service.cadastrar(pacienteId, request);
        return new OdontogramaResponse(odontograma);
    }

    @PutMapping("/{odontogramaId}")
    public OdontogramaResponse atualizar(
            @PathVariable Long odontogramaId,
            @Valid @RequestBody OdontogramaRequest request) {
        Odontograma odontograma = service.atualizar(odontogramaId, request);
        return new OdontogramaResponse(odontograma);
    }

    @DeleteMapping("/{odontogramaId}")
    public void excluir(@PathVariable Long odontogramaId) {
        service.excluir(odontogramaId);
    }
}