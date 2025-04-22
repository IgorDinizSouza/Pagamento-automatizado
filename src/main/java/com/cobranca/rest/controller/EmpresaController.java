package com.cobranca.rest.controller;

import com.cobranca.rest.config.JwtUtil;
import com.cobranca.rest.dto.EmpresaCadastroRequest;
import com.cobranca.rest.dto.EmpresaResponse;
import com.cobranca.rest.dto.EmpresaUpdateRequest;
import com.cobranca.rest.model.Empresa;
import com.cobranca.rest.repository.EmpresaRepository;
import com.cobranca.rest.service.EmpresaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;
    private final EmpresaRepository empresaRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public EmpresaController(EmpresaService empresaService,
                             EmpresaRepository empresaRepository,
                             JwtUtil jwtUtil,
                             PasswordEncoder passwordEncoder) {
        this.empresaService = empresaService;
        this.empresaRepository = empresaRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<EmpresaResponse> cadastrar(@RequestBody EmpresaCadastroRequest request) {
        EmpresaResponse response = empresaService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/logada")
    public ResponseEntity<?> buscarEmpresaLogada(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token ausente");
        }

        String login = jwtUtil.getLoginFromToken(token.replace("Bearer ", ""));
        Optional<Empresa> empresa = empresaRepository.findByLogin(login);

        return empresa.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<?> atualizarEmpresa(@RequestBody EmpresaUpdateRequest dto, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token ausente");
        }

        String login = jwtUtil.getLoginFromToken(token.replace("Bearer ", ""));
        Optional<Empresa> optional = empresaRepository.findByLogin(login);

        if (optional.isEmpty()) return ResponseEntity.notFound().build();

        Empresa empresa = optional.get();
        empresa.setNome(dto.getNome());
        empresa.setEmail(dto.getEmail());
        empresa.setLogin(dto.getLogin());

        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            empresa.setSenha(passwordEncoder.encode(dto.getSenha()));
        }

        empresaRepository.save(empresa);
        return ResponseEntity.ok("Atualizado com sucesso");
    }
}
