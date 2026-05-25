package br.com.prontuario.api.controller;

import br.com.prontuario.api.dto.LoginRequest;
import br.com.prontuario.api.dto.LoginResponse;
import br.com.prontuario.api.dto.TrocaSenhaObrigatoriaRequest;
import br.com.prontuario.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return service.login(request);
    }

    @PutMapping("/troca-senha-obrigatoria")
    public void trocarSenhaObrigatoria(
            @Valid @RequestBody TrocaSenhaObrigatoriaRequest request) {
        service.trocarSenhaObrigatoria(request);
    }
}