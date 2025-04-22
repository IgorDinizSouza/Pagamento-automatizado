package com.cobranca.rest.service;

import com.cobranca.rest.model.Empresa;
import com.cobranca.rest.repository.EmpresaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmpresaMonitoramentoService {

    private final EmpresaRepository empresaRepository;

    public EmpresaMonitoramentoService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @Scheduled(cron = "0 0 2 * * *") // todo dia às 2h da manhã
    public void verificarEmpresasInadimplentes() {
        List<Empresa> inadimplentes = empresaRepository
                .findByAssinaturaPagaFalseAndVencimentoAssinaturaBefore(LocalDate.now());

        for (Empresa empresa : inadimplentes) {
            empresa.setAtiva(false);
        }

        empresaRepository.saveAll(inadimplentes);
    }
}
