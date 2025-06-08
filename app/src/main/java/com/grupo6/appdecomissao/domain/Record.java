package com.grupo6.appdecomissao.domain;

import com.google.gson.annotations.SerializedName;

public class Record {

    private final String id;
    @SerializedName("pessoaNome")
    private final String clientName;

    // Consultor responsável por esse registro
    @SerializedName("responsavel")
    private final String responsibleId;

    // Estágio do processo que o registro se encontra
    @SerializedName("etapaNome")
    private final String stage;

    // Indica se o lead foi "ganho", "perdido" ou está "em andamento"
    @SerializedName("statusNome")
    private final String status;
    @SerializedName("responsavelNome")
    private final String responsibleName;

    public Record(String id, String clientName, String responsibleId, String stage, String status, String responsibleName) {
        this.id = id;
        this.clientName = clientName;
        this.responsibleId = responsibleId;
        this.stage = stage;
        this.status = status;
        this.responsibleName = responsibleName;
    }

    // Métodos de get
    public String getId() { return id; }
    public String getClientName() { return clientName; }
    public String getResponsibleId() { return responsibleId; }
    public String getStage() { return stage; }
    public String getStatus() { return status; }
    public String getResponsavelNome() { return responsibleName;}

}