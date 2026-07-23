package com.example.senannonces.api;

import com.example.senannonces.models.Annonce;
import com.example.senannonces.models.AuthResponse;
import com.example.senannonces.models.Category;
import com.example.senannonces.models.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiService {

    @POST("api/auth/register")
    Call<AuthResponse> register(@Body Map<String, String> body);

    @POST("api/auth/login")
    Call<AuthResponse> login(@Body Map<String, String> body);

    @GET("api/auth/me")
    Call<User> getProfile(@Header("Authorization") String token);

    @GET("api/categories")
    Call<List<Category>> getCategories();

    @GET("api/annonces")
    Call<List<Annonce>> getAnnonces(@QueryMap Map<String, String> options);

    @GET("api/annonces/{id}")
    Call<Annonce> getAnnonceDetail(@Path("id") String id);

    @POST("api/annonces")
    Call<Annonce> publishAnnonce(@Header("Authorization") String token, @Body Map<String, Object> body);
}
