package com.example.senannonces;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.senannonces.api.ApiClient;
import com.example.senannonces.api.ApiService;
import com.example.senannonces.models.Annonce;
import com.example.senannonces.models.ErrorResponse;
import com.example.senannonces.utils.InsetsUtils;
import com.example.senannonces.utils.NetworkUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnnonceDetailActivity extends AppCompatActivity {

    private ImageView ivImage;
    private TextView tvCategorie, tvTitre, tvPrix, tvQuartier, tvDate, tvDescription, tvVendeur, tvTelephone;
    private MaterialButton btnCall;
    private ProgressBar progressDetail;
    private MaterialToolbar toolbar;
    private View contentView, errorView;
    private TextView tvErrorTitle, tvErrorMessage;
    private MaterialButton btnRetry;
    private ApiService apiService;
    private Annonce currentAnnonce;
    private String annonceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InsetsUtils.enableEdgeToEdge(this);
        setContentView(R.layout.activity_annonce_detail);
        InsetsUtils.applySafeArea(findViewById(android.R.id.content));

        apiService = ApiClient.getApiService();

        ivImage = findViewById(R.id.iv_detail_image);
        tvCategorie = findViewById(R.id.tv_detail_categorie);
        tvTitre = findViewById(R.id.tv_detail_titre);
        tvPrix = findViewById(R.id.tv_detail_prix);
        tvQuartier = findViewById(R.id.tv_detail_quartier);
        tvDate = findViewById(R.id.tv_detail_date);
        tvDescription = findViewById(R.id.tv_detail_description);
        tvVendeur = findViewById(R.id.tv_detail_vendeur);
        tvTelephone = findViewById(R.id.tv_detail_telephone);
        btnCall = findViewById(R.id.btn_call);
        progressDetail = findViewById(R.id.progress_detail);
        toolbar = findViewById(R.id.toolbar);
        contentView = findViewById(R.id.content_view);
        errorView = findViewById(R.id.error_view);
        tvErrorTitle = errorView.findViewById(R.id.tv_error_title);
        tvErrorMessage = errorView.findViewById(R.id.tv_error_message);
        btnRetry = errorView.findViewById(R.id.btn_retry);

        toolbar.setNavigationOnClickListener(v -> finish());

        btnRetry.setOnClickListener(v -> {
            errorView.setVisibility(View.GONE);
            contentView.setVisibility(View.VISIBLE);
            loadAnnonceDetail(annonceId);
        });

        annonceId = getIntent().getStringExtra("annonce_id");
        if (annonceId != null && !annonceId.isEmpty()) {
            loadAnnonceDetail(annonceId);
        } else {
            finish();
        }

        btnCall.setOnClickListener(v -> {
            if (currentAnnonce != null && currentAnnonce.getTelephone() != null && !currentAnnonce.getTelephone().isEmpty()) {
                String phone = currentAnnonce.getTelephone().replaceAll("[^0-9+]", "");
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phone));
                startActivity(callIntent);
            }
        });
    }

    private void loadAnnonceDetail(String id) {
        progressDetail.setVisibility(View.VISIBLE);

        apiService.getAnnonceDetail(id).enqueue(new Callback<Annonce>() {
            @Override
            public void onResponse(Call<Annonce> call, Response<Annonce> response) {
                progressDetail.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    currentAnnonce = response.body();
                    displayAnnonce(currentAnnonce);
                } else {
                    String msg = "Annonce introuvable";
                    if (response.errorBody() != null) {
                        try {
                            ErrorResponse err = new com.google.gson.Gson().fromJson(
                                    response.errorBody().string(), ErrorResponse.class);
                            if (err != null) msg = err.getMessage();
                        } catch (Exception ignored) {}
                    }
                    showError("Erreur", msg);
                }
            }

            @Override
            public void onFailure(Call<Annonce> call, Throwable t) {
                progressDetail.setVisibility(View.GONE);
                String msg = NetworkUtils.isOnline(AnnonceDetailActivity.this)
                        ? "Erreur serveur"
                        : "Pas de connexion internet";
                showError("Impossible de charger", msg);
            }
        });
    }

    private void displayAnnonce(Annonce annonce) {
        tvTitre.setText(annonce.getTitre());
        tvPrix.setText(formatPrice(annonce.getPrix()));
        tvCategorie.setText(annonce.getCategorie());
        tvQuartier.setText(annonce.getQuartier());
        tvDate.setText(annonce.getDate() != null && annonce.getDate().length() >= 10 ? annonce.getDate().substring(0, 10) : "");
        tvDescription.setText(annonce.getDescription());
        tvVendeur.setText(annonce.getVendeur());
        tvTelephone.setText(annonce.getTelephone());

        if (annonce.getImageUrl() != null && !annonce.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(annonce.getImageUrl())
                    .transform(new CenterCrop(), new RoundedCorners(16))
                    .placeholder(R.drawable.bg_card)
                    .error(R.drawable.bg_card)
                    .into(ivImage);
        }
    }

    private void showError(String title, String message) {
        contentView.setVisibility(View.GONE);
        tvErrorTitle.setText(title);
        tvErrorMessage.setText(message);
        errorView.setVisibility(View.VISIBLE);
    }

    private String formatPrice(int price) {
        return String.format(Locale.FRANCE, "%,d FCFA", price).replace(',', ' ');
    }
}
