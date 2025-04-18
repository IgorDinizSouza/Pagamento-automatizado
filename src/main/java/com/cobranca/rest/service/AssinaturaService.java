package com.cobranca.rest.service;

import com.cobranca.rest.dto.AssinaturaDTO;
import com.cobranca.rest.model.Assinatura;
import com.cobranca.rest.model.Cliente;
import com.cobranca.rest.repository.AssinaturaRepository;
import com.cobranca.rest.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssinaturaService {

    private final AssinaturaRepository repository;
    private final ClienteRepository clienteRepository;

    public AssinaturaService(AssinaturaRepository repository, ClienteRepository clienteRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
    }

    public Assinatura criarAssinatura(AssinaturaDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Assinatura assinatura = new Assinatura();
        assinatura.setValor(dto.getValor());
        assinatura.setCiclo(dto.getCiclo());
        assinatura.setDataInicio(dto.getDataInicio());
        assinatura.setAtiva(dto.getAtiva());
        assinatura.setTipoPagamento(dto.getTipoPagamento());
        assinatura.setDiaVencimento(dto.getDiaVencimento());
        assinatura.setDescricao(dto.getDescricao());
        assinatura.setQuantidadeParcelas(dto.getQuantidadeParcelas());
        assinatura.setCliente(cliente);

        return repository.save(assinatura);
    }


    public List<Assinatura> listarPorCliente(Long clienteId) {
        return repository.findByClienteId(clienteId);
    }
}
