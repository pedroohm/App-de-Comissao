package com.grupo6.appdecomissao.domain;

public class Stage {
    private String id;
    private String titulo;
    private String indice;

    public Stage(String id, String titulo, String indice) {
        this.id = id;
        this.titulo = titulo;
        this.indice = indice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIndice() {
        return indice;
    }

    public void setIndice(String indice) {
        this.indice = indice;
    }

    // Métodos para compatibilidade com o código existente
    public String getName() {
        return titulo;
    }

    public String getProcessId() {
        return null; // As etapas da API Rubeus não têm processId específico
    }

    public int getOrder() {
        try {
            return Integer.parseInt(indice);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public boolean isActive() {
        return true; // Todas as etapas da API são consideradas ativas
    }

    @Override
    public String toString() {
        return titulo;
    }
} 