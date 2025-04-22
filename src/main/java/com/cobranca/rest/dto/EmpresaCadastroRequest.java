package com.cobranca.rest.dto;

public class EmpresaCadastroRequest {
    private String nome;
    private String email;
    private String login;
    private String senha;
    private String asaasToken;
    private String cpfCnpj;
    private boolean periodoTeste; // true = 3 dias de teste, false = pagamento imediato

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

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public boolean isPeriodoTeste() {
        return periodoTeste;
    }

    public void setPeriodoTeste(boolean periodoTeste) {
        this.periodoTeste = periodoTeste;
    }
}
