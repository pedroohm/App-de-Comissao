package com.grupo6.appdecomissao;

public class Goal {
    private String id;
    private String consultantId;
    private String description;
    private Double goalValue;
    private Double bonus;
    private Boolean achieved;

    public Goal(String id, String consultantId, String description, Double goalValue, Double bonus, Boolean achieved) {
        this.id = id;
        this.consultantId = consultantId;
        this.description = description;
        this.goalValue = goalValue;
        this.bonus = bonus;
        this.achieved = achieved;
    }

    public String getId() { return id; }

    public String getConsultantId() { return consultantId; }

    public String getDescription() { return description; }

    public Double getGoalValue() { return goalValue; }

    public Double getBonus() { return bonus; }

    public Boolean getAchieved() { return achieved; }
}
