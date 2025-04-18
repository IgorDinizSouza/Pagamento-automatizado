package com.cobranca.rest.controller;

import com.cobranca.rest.dto.ClienteDTO;
import com.cobranca.rest.model.Cliente;
import com.cobranca.rest.service.ClienteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @PostMapping
    public Cliente cadastrar(@RequestBody ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());
        cliente.setCpfCnpj(dto.getCpfCnpj());
        return service.salvar(cliente);
    }

    @GetMapping
    public List<Cliente> listar() {
        return service.listarTodos();
    }
}
