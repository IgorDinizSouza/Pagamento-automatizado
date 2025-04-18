package com.cobranca.rest.service;

import com.cobranca.rest.exception.CpfJaCadastradoException;
import com.cobranca.rest.model.Cliente;
import com.cobranca.rest.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    public Cliente salvar(Cliente cliente) {
        repository.findByCpfCnpj(cliente.getCpfCnpj()).ifPresent(c -> {
            throw new CpfJaCadastradoException(cliente.getCpfCnpj());
        });
        return repository.save(cliente);
    }

    public List<Cliente> listarTodos() {
        return repository.findAll();
    }
}
