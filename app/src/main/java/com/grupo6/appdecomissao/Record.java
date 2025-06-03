package com.grupo6.appdecomissao;

public class Record {
    private String id;
    private String clientName;

    // Consultor responsável por esse registro
    private String responsibleName;

    // Estágio do processo que o registro se encontra
    private String stage;

    public Record(String id, String clientName, String responsibleName, String stage) {
        this.id = id;
        this.clientName = clientName;
        this.responsibleName = responsibleName;
        this.stage = stage;
    }

    // Métodos de get
    public String getId() { return id; }
    public String getClientName() { return clientName; }
    public String getResponsibleName() { return responsibleName; }
    public String getStage() { return stage; }
}
