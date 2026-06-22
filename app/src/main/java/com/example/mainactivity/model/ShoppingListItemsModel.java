package com.example.mainactivity.model;

public class ShoppingListItemsModel {
    private String id, vare;
    private boolean isChecked;

    public ShoppingListItemsModel(String id, String vare, boolean isChecked) {
        this.id = id;
        this.vare = vare;
        this.isChecked = isChecked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItem() {
        return vare;
    }

    public boolean isChecked() {
        return isChecked;
    }
}
