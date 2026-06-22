package com.example.mainactivity.model;

public class MealPlanListModel {
    private int subMealPlanID;
    private String day, food;

    public MealPlanListModel(int subMealPlanID, String day, String food) {
        this.subMealPlanID = subMealPlanID;
        this.day = day;
        this.food = food;
    }

    public int getSubMealPlanID() {
        return subMealPlanID;
    }

    public String getDay() {
        return day;
    }

    public String getFood() {
        return food;
    }
}
