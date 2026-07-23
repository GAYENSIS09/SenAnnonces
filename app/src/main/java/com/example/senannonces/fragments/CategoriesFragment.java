package com.example.senannonces.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.senannonces.R;
import com.example.senannonces.adapters.CategoryGridAdapter;
import com.example.senannonces.api.ApiClient;
import com.example.senannonces.api.ApiService;
import com.example.senannonces.models.Category;
import com.example.senannonces.models.ErrorResponse;
import com.example.senannonces.utils.NetworkUtils;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriesFragment extends Fragment {

    private RecyclerView rvCategories;
    private ProgressBar progressCategories;
    private View errorView;
    private TextView tvErrorTitle, tvErrorMessage;
    private MaterialButton btnRetry;
    private CategoryGridAdapter adapter;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = ApiClient.getApiService();
        rvCategories = view.findViewById(R.id.rv_categories);
        progressCategories = view.findViewById(R.id.progress_categories);
        errorView = view.findViewById(R.id.error_view);
        tvErrorTitle = errorView.findViewById(R.id.tv_error_title);
        tvErrorMessage = errorView.findViewById(R.id.tv_error_message);
        btnRetry = errorView.findViewById(R.id.btn_retry);

        btnRetry.setOnClickListener(v -> {
            hideError();
            loadCategories();
        });

        adapter = new CategoryGridAdapter(category -> {
            if (getActivity() != null) {
                ((com.google.android.material.bottomnavigation.BottomNavigationView) getActivity().findViewById(R.id.bottom_nav)).setSelectedItemId(R.id.nav_home);
                Bundle result = new Bundle();
                result.putString("category_id", category.getId());
                result.putString("category_name", category.getNom());
                getParentFragmentManager().setFragmentResult("category_filter", result);
            }
        });

        rvCategories.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvCategories.setAdapter(adapter);

        loadCategories();
    }

    private void loadCategories() {
        progressCategories.setVisibility(View.VISIBLE);
        rvCategories.setVisibility(View.GONE);

        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {
                if (!isAdded()) return;
                progressCategories.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    adapter.setCategories(response.body());
                    rvCategories.setVisibility(View.VISIBLE);
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
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                progressCategories.setVisibility(View.GONE);
                String msg = NetworkUtils.isOnline(requireContext())
                        ? "Erreur serveur"
                        : "Pas de connexion internet";
                showError("Impossible de charger", msg);
            }
        });
    }

    private void showError(String title, String message) {
        if (!isAdded()) return;
        tvErrorTitle.setText(title);
        tvErrorMessage.setText(message);
        errorView.setVisibility(View.VISIBLE);
        rvCategories.setVisibility(View.GONE);
    }

    private void hideError() {
        errorView.setVisibility(View.GONE);
        rvCategories.setVisibility(View.VISIBLE);
    }
}
