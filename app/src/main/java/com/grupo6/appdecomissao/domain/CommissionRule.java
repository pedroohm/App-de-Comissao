package com.grupo6.appdecomissao.domain;

import java.util.Set;

public class CommissionRule {
    private final String id;
    private final String processId;
    private final String stage;
    private String name;
    private String description;
    Set<String> assignedConsultantIds;
    private String ruleType;
    // private String goalType;
    private double commissionPercentage;

    public CommissionRule(String id, String processId, String stage, String name, String description, Set<String> assignedConsultantIds, String ruleType, String goalType, double commissionPercentage) {
        this.id = id;
        this.processId = processId;
        this.stage = stage;
        this.name = name;
        this.description = description;
        this.assignedConsultantIds = assignedConsultantIds;
        this.ruleType = ruleType;
        this.commissionPercentage = commissionPercentage;
    }

    public String getId() { return id; }
    public String getProcessId() { return processId; }
    public String getStage() { return stage; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Set<String> getAssignedConsultantIds() { return assignedConsultantIds; }
    public String getRuleType() { return ruleType; }
    // public String getGoalType() { return goalType; }
    public double getCommissionPercentage() { return commissionPercentage; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
}