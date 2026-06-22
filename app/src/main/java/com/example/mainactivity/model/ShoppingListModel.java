package com.example.mainactivity.model;

public class ShoppingListModel {
    private String tittel, id, navn;
    private Integer familieID;

    public ShoppingListModel(String nr, String id, Integer familieID, String navn) {
        // Setter variablene
        this.tittel = nr;
        this.id = id;
        this.familieID = familieID;
        this.navn = navn;
    }

    public String getTittel() {
        return tittel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getFamilyID() {
        return familieID;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }
}

