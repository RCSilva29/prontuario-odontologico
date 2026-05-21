package br.com.prontuario.api.controller;

import br.com.prontuario.api.dto.AnexoResponse;
import br.com.prontuario.api.entity.Anexo;
import br.com.prontuario.api.service.AnexoService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/pacientes/{pacienteId}/anexos")
public class AnexoController {

    private final AnexoService service;

    public AnexoController(AnexoService service) {
        this.service = service;
    }

    @GetMapping
    public List<AnexoResponse> listar(@PathVariable Long pacienteId) {
        return service.listarPorPaciente(pacienteId)
                .stream()
                .map(AnexoResponse::new)
                .toList();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AnexoResponse upload(
            @PathVariable Long pacienteId,
            @RequestParam("arquivo") MultipartFile arquivo) {
        Anexo anexo = service.salvar(pacienteId, arquivo);
        return new AnexoResponse(anexo);
    }

    @GetMapping("/{anexoId}/download")
    public ResponseEntity<Resource> download(@PathVariable Long anexoId) {
        Anexo anexo = service.buscarPorId(anexoId);
        Resource arquivo = service.baixar(anexoId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + anexo.getNomeOriginal() + "\"")
                .contentType(MediaType.parseMediaType(anexo.getTipoConteudo()))
                .body(arquivo);
    }

    @DeleteMapping("/{anexoId}")
    public void excluir(@PathVariable Long anexoId) {
        service.excluir(anexoId);
    }
}