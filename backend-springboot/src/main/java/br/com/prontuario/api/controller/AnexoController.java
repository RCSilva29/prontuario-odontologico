package br.com.prontuario.api.controller;

import br.com.prontuario.api.dto.AnexoResponse;
import br.com.prontuario.api.entity.Anexo;
import br.com.prontuario.api.service.AnexoService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public List<AnexoResponse> listar(
            @PathVariable Long pacienteId,
            Authentication authentication) {
        return service.listarPorPaciente(
                pacienteId,
                obterEmailUsuarioLogado(authentication))
                .stream()
                .map(AnexoResponse::new)
                .toList();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AnexoResponse upload(
            @PathVariable Long pacienteId,
            @RequestParam("arquivo") MultipartFile arquivo,
            Authentication authentication) {
        Anexo anexo = service.salvar(
                pacienteId,
                arquivo,
                obterEmailUsuarioLogado(authentication));

        return new AnexoResponse(anexo);
    }

    @GetMapping("/{anexoId}/download")
    public ResponseEntity<Resource> download(
            @PathVariable Long pacienteId,
            @PathVariable Long anexoId,
            Authentication authentication) {
        Anexo anexo = service.buscarPorId(
                pacienteId,
                anexoId,
                obterEmailUsuarioLogado(authentication));

        Resource arquivo = service.baixar(
                pacienteId,
                anexoId,
                obterEmailUsuarioLogado(authentication));

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + anexo.getNomeOriginal() + "\"")
                .contentType(MediaType.parseMediaType(anexo.getTipoConteudo()))
                .body(arquivo);
    }

    @DeleteMapping("/{anexoId}")
    public void excluir(
            @PathVariable Long pacienteId,
            @PathVariable Long anexoId,
            Authentication authentication) {
        service.excluir(
                pacienteId,
                anexoId,
                obterEmailUsuarioLogado(authentication));
    }

    private String obterEmailUsuarioLogado(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Usuário autenticado não identificado");
        }

        return authentication.getName();
    }
}