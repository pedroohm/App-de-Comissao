package com.grupo6.appdecomissao.domain;

public class Process {
    private String id;
    private String nome;
    private String statusPositivo;
    private String statusNegativo;
    private String arrastarOportunidade;
    private String voltarEtapa;

    public Process(String id, String nome, String statusPositivo, String statusNegativo, String arrastarOportunidade, String voltarEtapa) {
        this.id = id;
        this.nome = nome;
        this.statusPositivo = statusPositivo;
        this.statusNegativo = statusNegativo;
        this.arrastarOportunidade = arrastarOportunidade;
        this.voltarEtapa = voltarEtapa;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getStatusPositivo() {
        return statusPositivo;
    }

    public void setStatusPositivo(String statusPositivo) {
        this.statusPositivo = statusPositivo;
    }

    public String getStatusNegativo() {
        return statusNegativo;
    }

    public void setStatusNegativo(String statusNegativo) {
        this.statusNegativo = statusNegativo;
    }

    public String getArrastarOportunidade() {
        return arrastarOportunidade;
    }

    public void setArrastarOportunidade(String arrastarOportunidade) {
        this.arrastarOportunidade = arrastarOportunidade;
    }

    public String getVoltarEtapa() {
        return voltarEtapa;
    }

    public void setVoltarEtapa(String voltarEtapa) {
        this.voltarEtapa = voltarEtapa;
    }

    public String getName() {
        return nome;
    }

    public boolean isActive() {
        return true;
    }

    @Override
    public String toString() {
        return nome;
    }
} 