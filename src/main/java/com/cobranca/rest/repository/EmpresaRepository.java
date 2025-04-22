package com.cobranca.rest.repository;

import com.cobranca.rest.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Optional<Empresa> findByLogin(String login);
    Optional<Empresa> findByEmail(String email);
    List<Empresa> findByAssinaturaPagaFalseAndVencimentoAssinaturaBefore(LocalDate data);


}

