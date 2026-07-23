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

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin, btnGoRegister;
    private TextView tvError;
    private ProgressBar progressLogin;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InsetsUtils.enableEdgeToEdge(this);
        setContentView(R.layout.activity_login);
        InsetsUtils.applySafeArea(findViewById(android.R.id.content));
        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(this);

        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnGoRegister = findViewById(R.id.btn_go_register);
        tvError = findViewById(R.id.tv_error);
        progressLogin = findViewById(R.id.progress_login);

        btnLogin.setOnClickListener(v -> attemptLogin());

        btnGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            tilEmail.setError("Email requis");
            return;
        }
        if (password.isEmpty()) {
            tilPassword.setError("Mot de passe requis");
            return;
        }

        tilEmail.setError(null);
        tilPassword.setError(null);
        tvError.setVisibility(View.GONE);

        showLoading(true);

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        apiService.login(body).enqueue(new Callback<AuthResponse>() {
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
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMsg = "Erreur de connexion";
                    if (response.code() == 401) {
                        errorMsg = "Identifiants invalides";
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
        progressLogin.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
        btnGoRegister.setEnabled(!show);
    }
}
