package br.com.prontuario.api.controller;

import br.com.prontuario.api.dto.AtestadoRequest;
import br.com.prontuario.api.dto.ReceituarioRequest;
import br.com.prontuario.api.service.DocumentoPdfService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

        String emailUsuarioLogado = obterEmailUsuarioLogado(authentication);

        byte[] pdf = documentoPdfService.gerarAtestado(
                id,
                request,
                emailUsuarioLogado);

        HttpHeaders headers = criarHeadersPdf("atestado.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }

    @PostMapping("/pacientes/{id}/receituario")
    public ResponseEntity<byte[]> gerarReceituario(
            @PathVariable Long id,
            @RequestBody ReceituarioRequest request,
            Authentication authentication) {

        String emailUsuarioLogado = obterEmailUsuarioLogado(authentication);

        byte[] pdf = documentoPdfService.gerarReceituario(
                id,
                request,
                emailUsuarioLogado);

        HttpHeaders headers = criarHeadersPdf("receituario.pdf");

        return ResponseEntity.ok()
                .headers(headers)
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

    @SuppressWarnings("unused")
    private String nomeArquivo(String prefixo, String nomePaciente) {
        String nomeNormalizado = nomePaciente == null || nomePaciente.isBlank()
                ? "paciente"
                : nomePaciente;

        nomeNormalizado = Normalizer.normalize(nomeNormalizado, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-zA-Z0-9]+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");

        String data = LocalDate.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));

        return prefixo + "_" + nomeNormalizado + "_" + data + ".pdf";
    }
}