package com.grupo6.appdecomissao;

import java.util.Set;

public class CommissionRule {
    private Integer id;
    private String name;
    private String description;
    Set<String> assignedConsultantIds;
    private String ruleType;
    private String goalType;
    private double bonusPercentage;

    public CommissionRule(Integer id, String name, String description, Set<String> assignedConsultantIds, String ruleType, String goalType, double bonusPercentage) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.assignedConsultantIds = assignedConsultantIds;
        this.ruleType = ruleType;
        this.goalType = goalType;
        this.bonusPercentage = bonusPercentage;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Set<String> assignedConsultantIds() { return assignedConsultantIds; }
    public String getRuleType() { return ruleType; }
    public String getGoalType() { return goalType; }
    public double getBonusPercentage() { return bonusPercentage; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
}
