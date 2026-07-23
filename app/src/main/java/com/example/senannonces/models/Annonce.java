package com.example.senannonces.models;

import com.google.gson.annotations.SerializedName;

public class Annonce {
    @SerializedName("id")
    private String id;

    @SerializedName("titre")
    private String titre;

    @SerializedName("prix")
    private int prix;

    @SerializedName("categorie")
    private String categorie;

    @SerializedName("quartier")
    private String quartier;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("vendeur")
    private String vendeur;

    @SerializedName("date")
    private String date;

    @SerializedName("description")
    private String description;

    @SerializedName("telephone")
    private String telephone;

    public Annonce() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public int getPrix() { return prix; }
    public void setPrix(int prix) { this.prix = prix; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public String getQuartier() { return quartier; }
    public void setQuartier(String quartier) { this.quartier = quartier; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getVendeur() { return vendeur; }
    public void setVendeur(String vendeur) { this.vendeur = vendeur; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
}
