package com.example.mainactivity.model;

public class MealPlanModel {
    // Variabler
    private int meal_planID;
    private int week;
    private String fromDate, toDate;

    public MealPlanModel(int meal_planID, int week, String fromDate, String toDate) {
        this.meal_planID = meal_planID;
        this.week = week;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public int getMealPlanID() {
        return meal_planID;
    }

    public int getWeek() {
        return week;
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return toDate;
    }
}
