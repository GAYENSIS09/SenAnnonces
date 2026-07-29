package com.example.senannonces;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.senannonces.adapters.AnnonceAdapter;
import com.example.senannonces.api.ApiClient;
import com.example.senannonces.api.ApiService;
import com.example.senannonces.models.Annonce;
import com.example.senannonces.models.Category;
import com.example.senannonces.models.ErrorResponse;
import com.example.senannonces.utils.InsetsUtils;
import com.example.senannonces.utils.NetworkUtils;
import com.example.senannonces.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvAnnonces;
    private LinearLayout llCategories;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressAnnonces;
    private TextView tvEmpty, tvResultsCount;
    private EditText etSearch;
    private Spinner spinnerSort;
    private View errorView;
    private TextView tvErrorTitle, tvErrorMessage;
    private MaterialButton btnRetry;

    private AnnonceAdapter annonceAdapter;
    private ApiService apiService;
    private List<Category> allCategories = new ArrayList<>();
    private SessionManager sessionManager;

    private String selectedCategoryId = null;
    private String currentSort = "recent";
    private String currentSearch = "";
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final long SEARCH_DELAY = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InsetsUtils.enableEdgeToEdge(this);
        setContentView(R.layout.activity_home);
        InsetsUtils.applySafeAreaTop(findViewById(android.R.id.content));

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_publish) {
                startActivity(new Intent(this, PublishAnnonceActivity.class));
                return true;
            } else if (id == R.id.action_logout) {
                sessionManager.clearSession();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });

        rvAnnonces = findViewById(R.id.rv_annonces);
        llCategories = findViewById(R.id.ll_categories);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        progressAnnonces = findViewById(R.id.progress_annonces);
        tvEmpty = findViewById(R.id.tv_empty);
        tvResultsCount = findViewById(R.id.tv_results_count);
        etSearch = findViewById(R.id.et_search);
        spinnerSort = findViewById(R.id.spinner_sort);
        errorView = findViewById(R.id.error_view);
        tvErrorTitle = errorView.findViewById(R.id.tv_error_title);
        tvErrorMessage = errorView.findViewById(R.id.tv_error_message);
        btnRetry = errorView.findViewById(R.id.btn_retry);

        btnRetry.setOnClickListener(v -> {
            hideError();
            loadCategories();
            loadAnnonces();
        });

        setupRecyclerView();
        setupSortSpinner();
        setupSearch();
        setupSwipeRefresh();
        loadCategories();
        loadAnnonces();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAnnonces();
    }

    private void setupRecyclerView() {
        annonceAdapter = new AnnonceAdapter(this, annonce -> {
            Intent intent = new Intent(HomeActivity.this, AnnonceDetailActivity.class);
            intent.putExtra("annonce_id", annonce.getId());
            startActivity(intent);
        });
        rvAnnonces.setLayoutManager(new LinearLayoutManager(this));
        rvAnnonces.setAdapter(annonceAdapter);
    }

    private void setupSortSpinner() {
        String[] sortOptions = {"Récent", "Prix croissant", "Prix décroissant"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, sortOptions);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerSort.setAdapter(adapter);
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: currentSort = "recent"; break;
                    case 1: currentSort = "prix_asc"; break;
                    case 2: currentSort = "prix_desc"; break;
                }
                loadAnnonces();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> {
                    currentSearch = s.toString().trim();
                    loadAnnonces();
                };
                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.acid, null));
        swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.off_black, null));
        swipeRefresh.setOnRefreshListener(this::loadAnnonces);
    }

    private void loadCategories() {
        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allCategories.clear();

                    Category all = new Category(null, "Toutes", "\uD83D\uDD0D");
                    allCategories.add(all);
                    allCategories.addAll(response.body());

                    buildCategoryChips();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {}
        });
    }

    private void buildCategoryChips() {
        llCategories.removeAllViews();
        for (int i = 0; i < allCategories.size(); i++) {
            Category cat = allCategories.get(i);
            View chipView = LayoutInflater.from(this).inflate(R.layout.item_category, llCategories, false);

            TextView tvEmoji = chipView.findViewById(R.id.tv_emoji);
            TextView tvNom = chipView.findViewById(R.id.tv_nom);
            LinearLayout container = chipView.findViewById(R.id.ll_category);

            tvEmoji.setText(cat.getEmoji());
            tvNom.setText(cat.getNom());

            int finalI = i;
            chipView.setOnClickListener(v -> {
                selectedCategoryId = (finalI == 0) ? null : cat.getId();
                buildCategoryChips();
                loadAnnonces();
            });

            boolean isSelected = (finalI == 0 && selectedCategoryId == null)
                    || (selectedCategoryId != null && selectedCategoryId.equals(cat.getId()) && finalI != 0);

            if (isSelected) {
                container.setBackgroundResource(R.drawable.bg_category_chip_selected);
                tvNom.setTextColor(getResources().getColor(R.color.acid, null));
            } else {
                container.setBackgroundResource(R.drawable.bg_category_chip);
                tvNom.setTextColor(getResources().getColor(R.color.white, null));
            }

            llCategories.addView(chipView);
        }
    }

    private void loadAnnonces() {
        progressAnnonces.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        rvAnnonces.setVisibility(View.VISIBLE);

        Map<String, String> options = new HashMap<>();
        options.put("sort", currentSort);

        if (!currentSearch.isEmpty()) {
            options.put("search", currentSearch);
        }
        if (selectedCategoryId != null) {
            options.put("categorie", selectedCategoryId);
        }

        apiService.getAnnonces(options).enqueue(new Callback<List<Annonce>>() {
            @Override
            public void onResponse(Call<List<Annonce>> call, Response<List<Annonce>> response) {
                progressAnnonces.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Annonce> result = response.body();
                    annonceAdapter.setAnnonces(result);
                    tvResultsCount.setText(result.size() + " annonce" + (result.size() != 1 ? "s" : ""));
                    tvEmpty.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);
                    rvAnnonces.setVisibility(result.isEmpty() ? View.GONE : View.VISIBLE);
                } else {
                    String msg = "Erreur serveur";
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
            public void onFailure(Call<List<Annonce>> call, Throwable t) {
                progressAnnonces.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                String msg = NetworkUtils.isOnline(HomeActivity.this)
                        ? "Erreur serveur"
                        : "Pas de connexion internet";
                showError("Impossible de charger", msg);
            }
        });
    }

    private void showError(String title, String message) {
        tvErrorTitle.setText(title);
        tvErrorMessage.setText(message);
        errorView.setVisibility(View.VISIBLE);
        rvAnnonces.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
    }

    private void hideError() {
        errorView.setVisibility(View.GONE);
        rvAnnonces.setVisibility(View.VISIBLE);
    }
}
