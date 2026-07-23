package com.example.senannonces.models;

import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("id")
    private String id;

    @SerializedName("nom")
    private String nom;

    @SerializedName("emoji")
    private String emoji;

    public Category() {}

    public Category(String id, String nom, String emoji) {
        this.id = id;
        this.nom = nom;
        this.emoji = emoji;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }
}
