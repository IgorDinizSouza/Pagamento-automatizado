package com.cobranca.rest.controller;

import com.cobranca.rest.dto.DashboardClienteDTO;
import com.cobranca.rest.dto.DashboardGraficoDTO;
import com.cobranca.rest.dto.DashboardResumoDTO;
import com.cobranca.rest.model.Cobranca;
import com.cobranca.rest.repository.CobrancaRepository;
import com.cobranca.rest.service.CobrancaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cobrancas")
public class CobrancaController {

    private final CobrancaService cobrancaService;
    private final CobrancaRepository cobrancaRepository;

    public CobrancaController(CobrancaService cobrancaService, CobrancaRepository cobrancaRepository) {
        this.cobrancaService = cobrancaService;
        this.cobrancaRepository = cobrancaRepository;
    }

    @PostMapping("/gerar")
    public String gerarCobrancas() {
        int total = cobrancaService.gerarCobrancas();
        return total + " cobranças geradas com sucesso.";
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Cobranca> listarPorCliente(@PathVariable Long clienteId) {
        return cobrancaRepository.findByAssinaturaClienteId(clienteId);
    }

    @PutMapping("/{id}/pagar")
    public String marcarComoPago(@PathVariable Long id) {
        boolean atualizado = cobrancaService.pagarCobranca(id);
        if (atualizado) {
            return "Cobrança marcada como paga.";
        } else {
            return "Cobrança não encontrada.";
        }
    }

    @GetMapping("/cliente/{clienteId}/pendentes")
    public List<Cobranca> listarPendentesPorCliente(@PathVariable Long clienteId) {
        return cobrancaRepository.findByAssinaturaClienteIdAndStatus(clienteId, "PENDENTE");
    }

    @PutMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id) {
        boolean cancelado = cobrancaService.cancelarCobranca(id);
        return cancelado ? "Cobrança cancelada com sucesso." : "Cobrança não encontrada.";
    }

    @GetMapping("/dashboard/resumo")
    public DashboardResumoDTO getResumo() {
        return cobrancaService.obterResumo();
    }

    @GetMapping("/dashboard/clientes")
    public List<DashboardClienteDTO> getResumoPorCliente() {
        return cobrancaService.obterResumoPorCliente();
    }

    @GetMapping("/dashboard/grafico-mensal")
    public List<DashboardGraficoDTO> getGraficoPorMes() {
        return cobrancaService.obterGraficoPagamentosPorMes();
    }

    @PostMapping("/gerar/cliente/{id}")
    public String gerarCobrancaPorCliente(@PathVariable Long id) {
        int total = cobrancaService.gerarCobrancaPorCliente(id);
        return total + " cobranças geradas para o cliente.";
    }

    @GetMapping("/cobrancas/{id}/status")
    public ResponseEntity<String> atualizarStatus(@PathVariable Long id) {
        return cobrancaService.atualizarStatusCobranca(id)
                .map(status -> ResponseEntity.ok("Status atualizado para: " + status))
                .orElse(ResponseEntity.notFound().build());
    }


}
