package com.grupo6.appdecomissao.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Goal implements Parcelable {
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

    protected Goal(Parcel in) {
        id = in.readString();
        assignedConsultantIds = new HashSet<>(in.createStringArrayList());
        description = in.readString();
        if (in.readByte() == 0) {
            goalValue = null;
        } else {
            goalValue = in.readDouble();
        }
        if (in.readByte() == 0) {
            bonus = null;
        } else {
            bonus = in.readDouble();
        }
        byte tmpAchieved = in.readByte();
        achieved = tmpAchieved == 0 ? null : tmpAchieved == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeStringList(new ArrayList<>(assignedConsultantIds));
        dest.writeString(description);
        if (goalValue == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(goalValue);
        }
        if (bonus == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(bonus);
        }
        dest.writeByte((byte) (achieved == null ? 0 : achieved ? 1 : 2));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return Objects.equals(id, goal.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Goal> CREATOR = new Creator<Goal>() {
        @Override
        public Goal createFromParcel(Parcel in) {
            return new Goal(in);
        }

        @Override
        public Goal[] newArray(int size) {
            return new Goal[size];
        }
    };
}