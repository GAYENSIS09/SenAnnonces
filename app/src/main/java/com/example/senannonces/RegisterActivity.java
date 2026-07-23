package com.example.senannonces;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.senannonces.api.ApiClient;
import com.example.senannonces.api.ApiService;
import com.example.senannonces.models.AuthResponse;
import com.example.senannonces.models.ErrorResponse;
import com.example.senannonces.models.User;
import com.example.senannonces.utils.InsetsUtils;
import com.example.senannonces.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilNom, tilEmail, tilTelephone, tilPassword;
    private TextInputEditText etNom, etEmail, etTelephone, etPassword;
    private MaterialButton btnRegister, btnGoLogin;
    private TextView tvError;
    private ProgressBar progressRegister;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InsetsUtils.enableEdgeToEdge(this);
        setContentView(R.layout.activity_register);
        InsetsUtils.applySafeArea(findViewById(android.R.id.content));

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(this);

        tilNom = findViewById(R.id.til_nom);
        tilEmail = findViewById(R.id.til_email);
        tilTelephone = findViewById(R.id.til_telephone);
        tilPassword = findViewById(R.id.til_password);
        etNom = findViewById(R.id.et_nom);
        etEmail = findViewById(R.id.et_email);
        etTelephone = findViewById(R.id.et_telephone);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);
        btnGoLogin = findViewById(R.id.btn_go_login);
        tvError = findViewById(R.id.tv_error);
        progressRegister = findViewById(R.id.progress_register);

        btnRegister.setOnClickListener(v -> attemptRegister());
        btnGoLogin.setOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        String nom = etNom.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String telephone = etTelephone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (nom.isEmpty()) { tilNom.setError("Nom requis"); return; }
        if (email.isEmpty()) { tilEmail.setError("Email requis"); return; }
        if (password.isEmpty()) { tilPassword.setError("Mot de passe requis"); return; }
        if (password.length() < 6) { tilPassword.setError("Minimum 6 caractères"); return; }

        tilNom.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tvError.setVisibility(View.GONE);

        showLoading(true);

        Map<String, String> body = new HashMap<>();
        body.put("nom", nom);
        body.put("email", email);
        body.put("password", password);
        if (!telephone.isEmpty()) {
            body.put("telephone", telephone);
        }

        apiService.register(body).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                showLoading(false);
                if (isFinishing()) return;

                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();
                    sessionManager.saveAuthToken(auth.getToken());
                    User user = auth.getUtilisateur();
                    if (user != null) {
                        sessionManager.saveUserInfo(
                            user.getId() != null ? user.getId() : "",
                            user.getNom() != null ? user.getNom() : "",
                            user.getEmail() != null ? user.getEmail() : "",
                            user.getTelephone() != null ? user.getTelephone() : ""
                        );
                    }
                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMsg = "Erreur d'inscription";
                    if (response.code() == 409) {
                        errorMsg = "Cet email est déjà utilisé";
                    } else if (response.errorBody() != null) {
                        try {
                            com.google.gson.Gson gson = new com.google.gson.Gson();
                            ErrorResponse errorResponse = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
                            if (errorResponse != null && errorResponse.getMessage() != null) {
                                errorMsg = errorResponse.getMessage();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    tvError.setText(errorMsg);
                    tvError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                showLoading(false);
                if (isFinishing()) return;
                tvError.setText("Erreur réseau: " + t.getMessage());
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showLoading(boolean show) {
        progressRegister.setVisibility(show ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!show);
        btnGoLogin.setEnabled(!show);
    }
}
