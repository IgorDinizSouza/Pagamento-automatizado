package com.cobranca.rest.controller;

import com.cobranca.rest.config.JwtUtil;
import com.cobranca.rest.dto.LoginRequest;
import com.cobranca.rest.dto.RecuperarSenhaRequest;
import com.cobranca.rest.dto.RedefinirSenhaRequest;
import com.cobranca.rest.dto.TokenResponse;
import com.cobranca.rest.model.Empresa;
import com.cobranca.rest.repository.EmpresaRepository;
import com.cobranca.rest.service.EmailService;
import com.cobranca.rest.service.EmpresaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final EmpresaService empresaService;

    public AuthController(EmpresaRepository empresaRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,EmailService emailService,EmpresaService empresaService) {
        this.empresaRepository = empresaRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
        this.empresaService = empresaService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Empresa empresa = empresaRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login inválido"));

        if (!empresa.getAtiva()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Empresa ainda não está ativa.");
        }

        if (!passwordEncoder.matches(request.getSenha(), empresa.getSenha())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Senha inválida");
        }

        String token = jwtUtil.generateToken(empresa.getLogin());
        return ResponseEntity.ok(new TokenResponse(token));
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<?> recuperarSenha(@RequestBody RecuperarSenhaRequest request) {
        Optional<Empresa> empresaOpt = empresaRepository.findByEmail(request.getEmail());

        if (empresaOpt.isPresent()) {
            String token = jwtUtil.generateResetToken(request.getEmail());
            String url = "http://localhost:5173/redefinir-senha?token=" + token; // ajuste conforme o frontend

            String mensagem = """
            <p>Olá,</p>
            <p>Você solicitou a redefinição de senha. Clique no link abaixo:</p>
            <p><a href="%s">Redefinir Senha</a></p>
            <p>Se não foi você, ignore este e-mail.</p>
        """.formatted(url);

            try {
                emailService.enviar(request.getEmail(), "Recuperar Senha", mensagem);
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Erro ao enviar e-mail");
            }
        }

        return ResponseEntity.ok().build(); // mesmo que não encontre, retorna OK (para segurança)
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<?> redefinirSenha(@RequestBody RedefinirSenhaRequest request) {
        try {
            empresaService.redefinirSenha(request);
            return ResponseEntity.ok("Senha redefinida com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token inválido ou expirado.");
        }
    }
}
