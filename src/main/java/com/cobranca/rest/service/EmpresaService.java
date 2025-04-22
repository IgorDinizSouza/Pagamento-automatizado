package com.cobranca.rest.service;

import com.cobranca.rest.client.AsaasClient;
import com.cobranca.rest.config.JwtUtil;
import com.cobranca.rest.dto.*;
import com.cobranca.rest.model.Empresa;
import com.cobranca.rest.repository.EmpresaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;
    private final AsaasClient asaasClient;
    private final JwtUtil jwtUtil;
    private static final Logger log = LoggerFactory.getLogger(EmpresaService.class);


    public EmpresaService(EmpresaRepository empresaRepository, PasswordEncoder passwordEncoder,AsaasClient asaasClient,JwtUtil jwtUtil) {
        this.empresaRepository = empresaRepository;
        this.passwordEncoder = passwordEncoder;
        this.asaasClient = asaasClient;
        this.jwtUtil = jwtUtil;
    }

    public EmpresaResponse cadastrar(EmpresaCadastroRequest request) {
        Empresa empresa = new Empresa();
        empresa.setNome(request.getNome());
        empresa.setEmail(request.getEmail());
        empresa.setLogin(request.getLogin());
        empresa.setSenha(passwordEncoder.encode(request.getSenha()));
        empresa.setAsaasToken(request.getAsaasToken());

        // Criar cliente no Asaas
        com.cobranca.rest.client.dto.AsaasCustomerRequest clienteReq = new com.cobranca.rest.client.dto.AsaasCustomerRequest();
        clienteReq.setName(request.getNome());
        clienteReq.setEmail(request.getEmail());
        clienteReq.setPhone("989999999"); // ou você adiciona esse campo no request
        clienteReq.setCpfCnpj("11144477735"); // idem

        AsaasCustomerResponse clienteResp = asaasClient.criarCliente(clienteReq);
        empresa.setAsaasCustomerId(clienteResp.getId());

        // Criar assinatura mensal
        AsaasSubscriptionRequest subReq = new AsaasSubscriptionRequest();
        subReq.setCustomer(clienteResp.getId());
        subReq.setNextDueDate(LocalDate.now().plusDays(1).toString());
        subReq.setValue(49.90);
        subReq.setDescription("Assinatura mensal para uso do sistema");

        AsaasSubscriptionResponse subResp = asaasClient.criarAssinatura(subReq);
        empresa.setAsaasSubscriptionId(subResp.getId());

        empresa.setAtiva(false);

        empresaRepository.save(empresa);

        return new EmpresaResponse(empresa.getId(), empresa.getNome(), empresa.getEmail());
    }

    public void redefinirSenha(RedefinirSenhaRequest request) {
        String email = jwtUtil.getLoginFromToken(request.getToken());

        Empresa empresa = empresaRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        empresa.setSenha(passwordEncoder.encode(request.getNovaSenha()));
        empresaRepository.save(empresa);
    }

    @Scheduled(cron = "0 0 3 * * *") // roda todo dia às 3h
    public void reativarEmpresasPagas() {
        List<Empresa> empresas = empresaRepository.findAll();

        for (Empresa empresa : empresas) {
            if (empresa.getAsaasSubscriptionId() == null || empresa.getAssinaturaPaga()) continue;

            try {
                AsaasSubscriptionResponse status = asaasClient.consultarAssinatura(empresa.getAsaasSubscriptionId());

                if ("ACTIVE".equalsIgnoreCase(status.getStatus())) {
                    empresa.setAtiva(true);
                    empresa.setAssinaturaPaga(true);
                    empresa.setVencimentoAssinatura(LocalDate.parse(status.getNextDueDate()));
                }

            } catch (Exception e) {

                log.warn("Erro ao verificar assinatura da empresa {}: {}", empresa.getId(), e.getMessage());
            }
        }

        empresaRepository.saveAll(empresas);
    }

}
