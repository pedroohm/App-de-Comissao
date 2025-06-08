package com.grupo6.appdecomissao.domain;

import java.util.Set;

public class Goal {
    private String id;
    Set<String> assignedConsultantIds;
    private String description;
    private Double goalValue;
    private Double bonus;
    private Boolean achieved;

    public Goal(String id, Set<String> assignedConsultantIds, String description, Double goalValue, Double bonus, Boolean achieved) {
        this.id = id;
        this.assignedConsultantIds = assignedConsultantIds;
        this.description = description;
        this.goalValue = goalValue;
        this.bonus = bonus;
        this.achieved = achieved;
    }

    public String getId() { return id; }

    public Set<String> getAssignedConsultantIds() { return assignedConsultantIds; }

    public String getDescription() { return description; }

    public Double getGoalValue() { return goalValue; }

    public Double getBonus() { return bonus; }

    public Boolean getAchieved() { return achieved; }
}