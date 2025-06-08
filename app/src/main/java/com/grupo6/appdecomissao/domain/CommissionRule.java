package com.grupo6.appdecomissao.domain;

import java.util.Set;

public class CommissionRule {
    private String id;
    private String name;
    private String description;
    Set<String> assignedConsultantIds;
    private String ruleType;
    private String goalType;
    private double bonusPercentage;

    public CommissionRule(String id, String name, String description, Set<String> assignedConsultantIds, String ruleType, String goalType, double bonusPercentage) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.assignedConsultantIds = assignedConsultantIds;
        this.ruleType = ruleType;
        this.goalType = goalType;
        this.bonusPercentage = bonusPercentage;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Set<String> getAssignedConsultantIds() { return assignedConsultantIds; }
    public String getRuleType() { return ruleType; }
    public String getGoalType() { return goalType; }
    public double getBonusPercentage() { return bonusPercentage; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
}