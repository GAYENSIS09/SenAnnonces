package com.example.senannonces.models;

import com.google.gson.annotations.SerializedName;

public class ApiError {
    @SerializedName("code")
    private String code;

    @SerializedName("message")
    private String message;

    public ApiError() {}

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
