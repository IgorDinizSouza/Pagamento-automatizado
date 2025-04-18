package com.cobranca.rest.dto;

public class DashboardResumoDTO {
    private Long totalCobrancas;
    private Long totalPagas;
    private Long totalPendentes;
    private Long totalCanceladas;

    private Double valorPagas;
    private Double valorPendentes;
    private Double valorCanceladas;

    public Long getTotalCobrancas() {
        return totalCobrancas;
    }

    public void setTotalCobrancas(Long totalCobrancas) {
        this.totalCobrancas = totalCobrancas;
    }

    public Long getTotalPagas() {
        return totalPagas;
    }

    public void setTotalPagas(Long totalPagas) {
        this.totalPagas = totalPagas;
    }

    public Long getTotalPendentes() {
        return totalPendentes;
    }

    public void setTotalPendentes(Long totalPendentes) {
        this.totalPendentes = totalPendentes;
    }

    public Long getTotalCanceladas() {
        return totalCanceladas;
    }

    public void setTotalCanceladas(Long totalCanceladas) {
        this.totalCanceladas = totalCanceladas;
    }

    public Double getValorPagas() {
        return valorPagas;
    }

    public void setValorPagas(Double valorPagas) {
        this.valorPagas = valorPagas;
    }

    public Double getValorPendentes() {
        return valorPendentes;
    }

    public void setValorPendentes(Double valorPendentes) {
        this.valorPendentes = valorPendentes;
    }

    public Double getValorCanceladas() {
        return valorCanceladas;
    }

    public void setValorCanceladas(Double valorCanceladas) {
        this.valorCanceladas = valorCanceladas;
    }
}
