package br.com.prontuario.api.controller;

import br.com.prontuario.api.dto.AtestadoRequest;
import br.com.prontuario.api.service.DocumentoPdfService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/documentos")
public class DocumentoController {

    private final DocumentoPdfService documentoPdfService;

    public DocumentoController(DocumentoPdfService documentoPdfService) {
        this.documentoPdfService = documentoPdfService;
    }

    @PostMapping("/pacientes/{id}/atestado")
    public ResponseEntity<byte[]> gerarAtestado(
            @PathVariable Long id,
            @RequestBody AtestadoRequest request,
            Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Usuário autenticado não identificado");
        }

        byte[] pdf = documentoPdfService.gerarAtestado(
                id,
                request,
                authentication.getName());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.inline()
                        .filename("atestado.pdf")
                        .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }
}