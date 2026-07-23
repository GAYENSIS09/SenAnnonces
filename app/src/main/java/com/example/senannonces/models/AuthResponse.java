package com.example.senannonces.models;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("utilisateur")
    private User utilisateur;

    @SerializedName("token")
    private String token;

    public AuthResponse() {}

    public User getUtilisateur() { return utilisateur; }
    public void setUtilisateur(User utilisateur) { this.utilisateur = utilisateur; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
