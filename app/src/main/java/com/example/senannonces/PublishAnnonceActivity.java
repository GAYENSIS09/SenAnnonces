package com.example.senannonces;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.senannonces.api.ApiClient;
import com.example.senannonces.api.ApiService;
import com.example.senannonces.models.Annonce;
import com.example.senannonces.models.Category;
import com.example.senannonces.models.ErrorResponse;
import com.example.senannonces.utils.InsetsUtils;
import com.example.senannonces.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublishAnnonceActivity extends AppCompatActivity {

    private TextInputLayout tilTitre, tilPrix, tilDescription, tilQuartier;
    private TextInputEditText etTitre, etPrix, etDescription, etQuartier;
    private Spinner spinnerCategorie;
    private MaterialButton btnPublish;
    private TextView tvError;
    private ProgressBar progressPublish;
    private MaterialToolbar toolbar;

    private ApiService apiService;
    private SessionManager sessionManager;
    private List<Category> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InsetsUtils.enableEdgeToEdge(this);
        setContentView(R.layout.activity_publish);
        InsetsUtils.applySafeArea(findViewById(android.R.id.content));

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(this);

        tilTitre = findViewById(R.id.til_titre);
        tilPrix = findViewById(R.id.til_prix);
        tilDescription = findViewById(R.id.til_description);
        tilQuartier = findViewById(R.id.til_quartier);
        etTitre = findViewById(R.id.et_titre);
        etPrix = findViewById(R.id.et_prix);
        etDescription = findViewById(R.id.et_description);
        etQuartier = findViewById(R.id.et_quartier);
        spinnerCategorie = findViewById(R.id.spinner_categorie);
        btnPublish = findViewById(R.id.btn_publish);
        tvError = findViewById(R.id.tv_error);
        progressPublish = findViewById(R.id.progress_publish);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(v -> finish());

        loadCategories();

        btnPublish.setOnClickListener(v -> attemptPublish());
    }

    private void loadCategories() {
        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body();
                    List<String> categoryNames = new ArrayList<>();
                    categoryNames.add("Sélectionner...");
                    for (Category cat : categories) {
                        categoryNames.add(cat.getEmoji() + " " + cat.getNom());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(PublishAnnonceActivity.this,
                            R.layout.item_spinner, categoryNames);
                    adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
                    spinnerCategorie.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {}
        });
    }

    private void attemptPublish() {
        String titre = etTitre.getText().toString().trim();
        String prixStr = etPrix.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String quartier = etQuartier.getText().toString().trim();
        int catPosition = spinnerCategorie.getSelectedItemPosition();

        if (titre.isEmpty()) { tilTitre.setError("Titre requis"); return; }
        if (prixStr.isEmpty()) { tilPrix.setError("Prix requis"); return; }
        if (catPosition == 0) { tvError.setText("Sélectionnez une catégorie"); tvError.setVisibility(View.VISIBLE); return; }
        if (description.isEmpty()) { tilDescription.setError("Description requise"); return; }
        if (quartier.isEmpty()) { tilQuartier.setError("Quartier requis"); return; }

        tilTitre.setError(null);
        tilPrix.setError(null);
        tilDescription.setError(null);
        tilQuartier.setError(null);
        tvError.setVisibility(View.GONE);

        showLoading(true);

        Map<String, Object> body = new HashMap<>();
        body.put("titre", titre);
        body.put("prix", Integer.parseInt(prixStr));
        body.put("categorie", categories.get(catPosition - 1).getId());
        body.put("description", description);
        body.put("quartier", quartier);

        String token = "Bearer " + sessionManager.getAuthToken();

        apiService.publishAnnonce(token, body).enqueue(new Callback<Annonce>() {
            @Override
            public void onResponse(Call<Annonce> call, Response<Annonce> response) {
                showLoading(false);
                if (isFinishing()) return;

                if (response.isSuccessful()) {
                    Toast.makeText(PublishAnnonceActivity.this, "Annonce publiée !", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMsg = "Erreur lors de la publication";
                    if (response.errorBody() != null) {
                        try {
                            com.google.gson.Gson gson = new com.google.gson.Gson();
                            ErrorResponse errorResponse = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
                            if (errorResponse != null && errorResponse.getMessage() != null) {
                                errorMsg = errorResponse.getMessage();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    tvError.setText(errorMsg);
                    tvError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Annonce> call, Throwable t) {
                showLoading(false);
                if (isFinishing()) return;
                tvError.setText("Erreur réseau: " + t.getMessage());
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showLoading(boolean show) {
        progressPublish.setVisibility(show ? View.VISIBLE : View.GONE);
        btnPublish.setEnabled(!show);
    }
}
