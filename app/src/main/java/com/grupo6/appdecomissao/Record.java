package com.grupo6.appdecomissao;

public class Record {
    private String id;
    private String clientName;

    // Consultor responsável por esse registro
    private String responsibleId;

    // Estágio do processo que o registro se encontra
    private String stage;

    public Record(String id, String clientName, String responsibleId, String stage) {
        this.id = id;
        this.clientName = clientName;
        this.responsibleId = responsibleId;
        this.stage = stage;
    }

    // Métodos de get
    public String getId() { return id; }
    public String getClientName() { return clientName; }
    public String getResponsibleId() { return responsibleId; }
    public String getStage() { return stage; }
}
