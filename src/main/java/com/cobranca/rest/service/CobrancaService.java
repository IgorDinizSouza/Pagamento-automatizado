package com.cobranca.rest.service;

import com.cobranca.rest.client.AsaasClient;
import com.cobranca.rest.client.dto.AsaasCustomerRequest;
import com.cobranca.rest.dto.*;
import com.cobranca.rest.model.Assinatura;
import com.cobranca.rest.model.Cliente;
import com.cobranca.rest.model.Cobranca;
import com.cobranca.rest.repository.AssinaturaRepository;
import com.cobranca.rest.repository.ClienteRepository;
import com.cobranca.rest.repository.CobrancaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class CobrancaService {

    private final CobrancaRepository cobrancaRepository;
    private final AssinaturaRepository assinaturaRepository;
    private final AsaasClient asaasClient;
    private final ClienteRepository clienteRepository;


    public CobrancaService(CobrancaRepository cobrancaRepository, AssinaturaRepository assinaturaRepository, AsaasClient asaasClient, ClienteRepository clienteRepository) {
        this.cobrancaRepository = cobrancaRepository;
        this.assinaturaRepository = assinaturaRepository;
        this.asaasClient = asaasClient;
        this.clienteRepository = clienteRepository;
    }

    public int gerarCobrancas() {
        List<Assinatura> assinaturasAtivas = assinaturaRepository.findAll()
                .stream()
                .filter(Assinatura::getAtiva)
                .toList();

        for (Assinatura assinatura : assinaturasAtivas) {
            Cliente cliente = assinatura.getCliente();

            if (cliente.getAsaasCustomerId() == null) {
                com.cobranca.rest.client.dto.AsaasCustomerRequest clienteReq = new com.cobranca.rest.client.dto.AsaasCustomerRequest();
                clienteReq.setName(cliente.getNome());
                clienteReq.setEmail(cliente.getEmail());
                clienteReq.setPhone(cliente.getTelefone());
                clienteReq.setCpfCnpj(cliente.getCpfCnpj());

                AsaasCustomerResponse clienteResp = asaasClient.criarCliente(clienteReq);
                cliente.setAsaasCustomerId(clienteResp.getId());
                clienteRepository.save(cliente);
            }

            // 👉 Calcula a data de vencimento no formato correto (ISO 8601)
            LocalDate dataVencimento = LocalDate.now()
                    .withDayOfMonth(1)
                    .plusMonths(1)
                    .withDayOfMonth(assinatura.getDiaVencimento());

            boolean jaExiste = cobrancaRepository.existsByAssinaturaIdAndDataVencimento(assinatura.getId(), dataVencimento);
            if (jaExiste) continue;

            // 👉 Monta requisição de pagamento
            AsaasPaymentRequest pagamentoReq = new AsaasPaymentRequest();
            pagamentoReq.setCustomer(cliente.getAsaasCustomerId());

            if (assinatura.getQuantidadeParcelas() != null && assinatura.getQuantidadeParcelas() > 0) {
                pagamentoReq.setInstallmentCount(assinatura.getQuantidadeParcelas());
                pagamentoReq.setInstallmentValue(assinatura.getValor());
            } else {
                pagamentoReq.setValue(assinatura.getValor());
            }

            pagamentoReq.setDueDate(dataVencimento.toString()); // ✅ formato yyyy-MM-dd
            pagamentoReq.setBillingType(assinatura.getTipoPagamento());
            pagamentoReq.setDescription(assinatura.getDescricao());

            // 👉 Cria cobrança via Asaas
            AsaasPaymentResponse pagamentoResp = asaasClient.criarCobranca(pagamentoReq);

            // 👉 Salva cobrança local
            Cobranca cobranca = new Cobranca();
            cobranca.setAssinatura(assinatura);
            cobranca.setDataVencimento(dataVencimento);
            cobranca.setValor(assinatura.getValor());
            cobranca.setStatus("PENDENTE");
            cobranca.setTipo(assinatura.getTipoPagamento());
            cobranca.setCodigoPagamento(pagamentoResp.getId());

            cobrancaRepository.save(cobranca);
        }

        return assinaturasAtivas.size();
    }


    private String gerarCodigoFicticio() {
        return "COD" + new Random().nextInt(999999);
    }

    public boolean pagarCobranca(Long cobrancaId) {
        return cobrancaRepository.findById(cobrancaId).map(cobranca -> {
            cobranca.setStatus("PAGO");
            cobranca.setDataPagamento(LocalDate.now());
            cobrancaRepository.save(cobranca);
            return true;
        }).orElse(false);
    }

    public DashboardResumoDTO obterResumo() {
        List<Cobranca> todas = cobrancaRepository.findAll();

        DashboardResumoDTO dto = new DashboardResumoDTO();
        dto.setTotalCobrancas((long) todas.size());

        dto.setTotalPagas(todas.stream().filter(c -> "PAGO".equals(c.getStatus())).count());
        dto.setTotalPendentes(todas.stream().filter(c -> "PENDENTE".equals(c.getStatus())).count());
        dto.setTotalCanceladas(todas.stream().filter(c -> "CANCELADO".equals(c.getStatus())).count());

        dto.setValorPagas(todas.stream().filter(c -> "PAGO".equals(c.getStatus())).mapToDouble(Cobranca::getValor).sum());
        dto.setValorPendentes(todas.stream().filter(c -> "PENDENTE".equals(c.getStatus())).mapToDouble(Cobranca::getValor).sum());
        dto.setValorCanceladas(todas.stream().filter(c -> "CANCELADO".equals(c.getStatus())).mapToDouble(Cobranca::getValor).sum());

        return dto;
    }

    public List<DashboardClienteDTO> obterResumoPorCliente() {
        List<Cobranca> cobrancas = cobrancaRepository.findAll();

        return cobrancas.stream()
                .collect(Collectors.groupingBy(c -> c.getAssinatura().getCliente()))
                .entrySet()
                .stream()
                .map(entry -> {
                    Cliente cliente = entry.getKey();
                    List<Cobranca> lista = entry.getValue();

                    DashboardClienteDTO dto = new DashboardClienteDTO();
                    dto.setClienteId(cliente.getId());
                    dto.setClienteNome(cliente.getNome());

                    dto.setTotalCobrancas((long) lista.size());
                    dto.setTotalPagas(lista.stream().filter(c -> "PAGO".equals(c.getStatus())).count());
                    dto.setTotalPendentes(lista.stream().filter(c -> "PENDENTE".equals(c.getStatus())).count());
                    dto.setTotalCanceladas(lista.stream().filter(c -> "CANCELADO".equals(c.getStatus())).count());

                    dto.setValorPagas(lista.stream().filter(c -> "PAGO".equals(c.getStatus())).mapToDouble(Cobranca::getValor).sum());
                    dto.setValorPendentes(lista.stream().filter(c -> "PENDENTE".equals(c.getStatus())).mapToDouble(Cobranca::getValor).sum());
                    dto.setValorCanceladas(lista.stream().filter(c -> "CANCELADO".equals(c.getStatus())).mapToDouble(Cobranca::getValor).sum());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<DashboardGraficoDTO> obterGraficoPagamentosPorMes() {
        return cobrancaRepository.findAll().stream()
                .filter(c -> "PAGO".equals(c.getStatus()) && c.getDataPagamento() != null)
                .collect(Collectors.groupingBy(
                        c -> c.getDataPagamento().getMonth().toString(), // ou .format(DateTimeFormatter.ofPattern("MM/yyyy"))
                        Collectors.summingDouble(Cobranca::getValor)
                ))
                .entrySet()
                .stream()
                .map(e -> new DashboardGraficoDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public int gerarCobrancaPorCliente(Long clienteId) {
        List<Assinatura> assinaturas = assinaturaRepository.findByClienteId(clienteId)
                .stream()
                .filter(Assinatura::getAtiva)
                .toList();

        for (Assinatura assinatura : assinaturas) {
            Cliente cliente = assinatura.getCliente();

            if (cliente.getAsaasCustomerId() == null) {
                AsaasCustomerRequest clienteReq = new AsaasCustomerRequest();
                clienteReq.setName(cliente.getNome());
                clienteReq.setEmail(cliente.getEmail());
                clienteReq.setPhone(cliente.getTelefone());
                clienteReq.setCpfCnpj(cliente.getCpfCnpj());

                AsaasCustomerResponse clienteResp = asaasClient.criarCliente(clienteReq);
                cliente.setAsaasCustomerId(clienteResp.getId());
                clienteRepository.save(cliente);
            }

            // ✅ Calcula data de vencimento válida para o Asaas
            LocalDate dataVencimento = LocalDate.now()
                    .withDayOfMonth(1)
                    .plusMonths(1)
                    .withDayOfMonth(assinatura.getDiaVencimento());

            boolean jaExiste = cobrancaRepository.existsByAssinaturaIdAndDataVencimento(assinatura.getId(), dataVencimento);
            if (jaExiste) continue;

            // ✅ Monta pagamento
            AsaasPaymentRequest pagamentoReq = new AsaasPaymentRequest();
            pagamentoReq.setCustomer(cliente.getAsaasCustomerId());

            if (assinatura.getQuantidadeParcelas() != null && assinatura.getQuantidadeParcelas() > 0) {
                pagamentoReq.setInstallmentCount(assinatura.getQuantidadeParcelas());
                pagamentoReq.setInstallmentValue(assinatura.getValor());
            } else {
                pagamentoReq.setValue(assinatura.getValor());
            }

            pagamentoReq.setDueDate(dataVencimento.toString()); // yyyy-MM-dd
            pagamentoReq.setBillingType(assinatura.getTipoPagamento());
            pagamentoReq.setDescription(assinatura.getDescricao());

            AsaasPaymentResponse pagamentoResp = asaasClient.criarCobranca(pagamentoReq);

            // ✅ Salva cobrança
            Cobranca cobranca = new Cobranca();
            cobranca.setAssinatura(assinatura);
            cobranca.setDataVencimento(dataVencimento);
            cobranca.setValor(assinatura.getValor());
            cobranca.setStatus("PENDENTE");
            cobranca.setTipo(assinatura.getTipoPagamento());
            cobranca.setCodigoPagamento(pagamentoResp.getId());

            cobrancaRepository.save(cobranca);
        }

        return assinaturas.size();
    }

    public Optional<String> atualizarStatusCobranca(Long cobrancaId) {
        return cobrancaRepository.findById(cobrancaId).map(cobranca -> {
            String paymentId = cobranca.getCodigoPagamento();
            AsaasPaymentResponse resposta = asaasClient.consultarPagamento(paymentId);

            if ("RECEIVED".equalsIgnoreCase(resposta.getStatus())) {
                cobranca.setStatus("PAGO");
                cobranca.setDataPagamento(LocalDate.parse(resposta.getPaymentDate()));
            } else if ("CANCELLED".equalsIgnoreCase(resposta.getStatus())) {
                cobranca.setStatus("CANCELADO");
            } else {
                cobranca.setStatus("PENDENTE");
            }

            cobrancaRepository.save(cobranca);
            return cobranca.getStatus();
        });
    }

    public boolean cancelarCobranca(Long cobrancaId) {
        return cobrancaRepository.findById(cobrancaId).map(cobranca -> {
            try {
                if (cobranca.getCodigoPagamento() != null && !cobranca.getCodigoPagamento().isBlank()) {
                    // Cancela a cobrança no Asaas
                    asaasClient.cancelarPagamento(cobranca.getCodigoPagamento());
                }

                // Atualiza o status local
                cobranca.setStatus("CANCELADO");
                cobrancaRepository.save(cobranca);
                return true;

            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }).orElse(false);
    }


}
