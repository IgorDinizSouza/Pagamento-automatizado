package com.cobranca.rest.controller;

import com.cobranca.rest.dto.AssinaturaDTO;
import com.cobranca.rest.model.Assinatura;
import com.cobranca.rest.service.AssinaturaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assinaturas")
public class AssinaturaController {

    private final AssinaturaService service;

    public AssinaturaController(AssinaturaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Assinatura> criar(@RequestBody AssinaturaDTO dto) {
        Assinatura assinatura = service.criarAssinatura(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(assinatura);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Assinatura>> listarPorCliente(@PathVariable Long clienteId) {
        List<Assinatura> assinaturas = service.listarPorCliente(clienteId);
        return ResponseEntity.ok(assinaturas);
    }
}
