package com.cobranca.rest.dto;

public class DashboardGraficoDTO {
    private String label;
    private Double valor;

    public DashboardGraficoDTO(String label, Double valor) {
        this.label = label;
        this.valor = valor;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
}
