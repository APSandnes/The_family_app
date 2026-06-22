package com.example.mainactivity.model;

public class FamilyBubbleModel {
    private String samtaleID, userToName, samtaleName;

    public FamilyBubbleModel(String samtaleID, String userToName, String samtaleName) {
        this.samtaleID = samtaleID;
        this.userToName = userToName;
        this.samtaleName = samtaleName;
    }

    public String getUserToName() {
        return userToName;
    }

    public String getIden() {
        return samtaleID;
    }

    public String getConversationName() {
        return samtaleName;
    }
}
