package com.grupo6.appdecomissao.domain;

import com.google.gson.annotations.SerializedName;

public class Record {

    @SerializedName("id")
    private final String id;
    @SerializedName("pessoaNome")
    private final String clientName;

    // Consultor responsável por esse registro
    @SerializedName("responsavel")
    private final String responsibleId;

    @SerializedName("processo")
    private final String processId;

    // Estágio do processo que o registro se encontra
    @SerializedName("etapaNome")
    private final String stage;

    // Indica se o lead foi "ganho", "perdido" ou está "em andamento"
    @SerializedName("statusNome")
    private final String status;

    @SerializedName("ofertaCursoNome")
    private final String offerName;

    @SerializedName("valorCurso")
    private final String offerValue;
    @SerializedName("responsavelNome")
    private final String responsibleName;
    @SerializedName("ultimaAlteracaoEtapa")
    private final String lastDate;

    public Record(String id, String clientName, String responsibleId, String processId, String stage, String status, String offerName, String offerValue, String responsibleName, String lastDate) {
        this.id = id;
        this.clientName = clientName;
        this.responsibleId = responsibleId;
        this.processId = processId;
        this.stage = stage;
        this.status = status;
        this.offerName = offerName;
        this.offerValue = offerValue;
        this.responsibleName = responsibleName;
        this.lastDate = lastDate;
    }

    // Métodos de get
    public String getId() { return id; }
    public String getClientName() { return clientName; }
    public String getResponsibleId() { return responsibleId; }
    public String getProcessId() { return processId; }
    public String getStage() { return stage; }
    public String getStatus() { return status; }
    public String getOfferName() { return offerName; }
    public String getOfferValue() { return offerValue; }
    public String getResponsibleName() { return responsibleName; }
    public String getLastDate() { return lastDate; }

}