package com.example.senannonces.models;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {
    @SerializedName("error")
    private ApiError error;

    public ErrorResponse() {}

    public ApiError getError() { return error; }
    public void setError(ApiError error) { this.error = error; }

    public String getMessage() {
        if (error != null) {
            return error.getMessage();
        }
        return "Erreur inconnue";
    }
}
