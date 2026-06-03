package br.com.prontuario.api.controller;

import br.com.prontuario.api.dto.AtestadoRequest;
import br.com.prontuario.api.dto.ProntuarioPdfRequest;
import br.com.prontuario.api.dto.ReceituarioRequest;
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

        byte[] pdf = documentoPdfService.gerarAtestado(
                id,
                request,
                obterEmailUsuarioLogado(authentication));

        return ResponseEntity.ok()
                .headers(criarHeadersPdf("atestado.pdf"))
                .body(pdf);
    }

    @PostMapping("/pacientes/{id}/receituario")
    public ResponseEntity<byte[]> gerarReceituario(
            @PathVariable Long id,
            @RequestBody ReceituarioRequest request,
            Authentication authentication) {

        byte[] pdf = documentoPdfService.gerarReceituario(
                id,
                request,
                obterEmailUsuarioLogado(authentication));

        return ResponseEntity.ok()
                .headers(criarHeadersPdf("receituario.pdf"))
                .body(pdf);
    }

    @PostMapping("/pacientes/{id}/prontuario")
    public ResponseEntity<byte[]> gerarProntuario(
            @PathVariable Long id,
            @RequestBody ProntuarioPdfRequest request,
            Authentication authentication) {

        byte[] pdf = documentoPdfService.gerarProntuario(
                id,
                request,
                obterEmailUsuarioLogado(authentication));

        return ResponseEntity.ok()
                .headers(criarHeadersPdf("prontuario.pdf"))
                .body(pdf);
    }

    private String obterEmailUsuarioLogado(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Usuário autenticado não identificado");
        }

        return authentication.getName();
    }

    private HttpHeaders criarHeadersPdf(String nomeArquivo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.inline()
                        .filename(nomeArquivo)
                        .build());

        return headers;
    }
}