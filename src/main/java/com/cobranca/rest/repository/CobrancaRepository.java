package com.cobranca.rest.repository;

import com.cobranca.rest.model.Cobranca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CobrancaRepository extends JpaRepository<Cobranca, Long> {
    List<Cobranca> findByAssinaturaClienteId(Long clienteId);
    List<Cobranca> findByAssinaturaClienteIdAndStatus(Long clienteId, String status);
    boolean existsByAssinaturaIdAndDataVencimento(Long assinaturaId, LocalDate dataVencimento);

}
