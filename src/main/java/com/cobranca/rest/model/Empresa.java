
package com.cobranca.rest.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "empresas")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String login;

    private String senha;

    @Column(name = "asaas_token")
    private String asaasToken;

    private LocalDate dataCriacao = LocalDate.now();

    private String asaasCustomerId;
    private String asaasSubscriptionId;
    private Boolean ativa = false;

    @Column(name = "vencimento_assinatura")
    private LocalDate vencimentoAssinatura;

    @Column(name = "assinatura_paga")
    private Boolean assinaturaPaga = false;

    @Column(name = "cpf_cnpj", unique = true)
    private String cpfCnpj;

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getAsaasCustomerId() {
        return asaasCustomerId;
    }

    public void setAsaasCustomerId(String asaasCustomerId) {
        this.asaasCustomerId = asaasCustomerId;
    }

    public String getAsaasSubscriptionId() {
        return asaasSubscriptionId;
    }

    public void setAsaasSubscriptionId(String asaasSubscriptionId) {
        this.asaasSubscriptionId = asaasSubscriptionId;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getAsaasToken() {
        return asaasToken;
    }

    public void setAsaasToken(String asaasToken) {
        this.asaasToken = asaasToken;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDate getVencimentoAssinatura() {
        return vencimentoAssinatura;
    }

    public void setVencimentoAssinatura(LocalDate vencimentoAssinatura) {
        this.vencimentoAssinatura = vencimentoAssinatura;
    }

    public Boolean getAssinaturaPaga() {
        return assinaturaPaga;
    }

    public void setAssinaturaPaga(Boolean assinaturaPaga) {
        this.assinaturaPaga = assinaturaPaga;
    }
}