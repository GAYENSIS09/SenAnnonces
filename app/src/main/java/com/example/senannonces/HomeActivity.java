package com.example.senannonces;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.senannonces.fragments.CategoriesFragment;
import com.example.senannonces.fragments.HomeFragment;
import com.example.senannonces.fragments.ProfileFragment;
import com.example.senannonces.utils.InsetsUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InsetsUtils.enableEdgeToEdge(this);
        setContentView(R.layout.activity_home);

        InsetsUtils.applySafeAreaTop(findViewById(android.R.id.content));

        try {
            bottomNav = findViewById(R.id.bottom_nav);

            if (savedInstanceState == null) {
                loadFragment(new HomeFragment());
            }

            bottomNav.setOnItemSelectedListener(item -> {
                try {
                    int itemId = item.getItemId();
                    if (itemId == R.id.nav_home) {
                        loadFragment(new HomeFragment());
                        return true;
                    } else if (itemId == R.id.nav_categories) {
                        loadFragment(new CategoriesFragment());
                        return true;
                    } else if (itemId == R.id.nav_publish) {
                        startActivity(new Intent(this, PublishAnnonceActivity.class));
                        return true;
                    } else if (itemId == R.id.nav_profile) {
                        loadFragment(new ProfileFragment());
                        return true;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Nav error", e);
                }
                return false;
            });
        } catch (Exception e) {
            Log.e(TAG, "onCreate error", e);
            throw new RuntimeException(e);
        }
    }

    private BottomNavigationView bottomNav;

    private void loadFragment(Fragment fragment) {
        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        } catch (Exception e) {
            Log.e(TAG, "loadFragment error", e);
        }
    }
}
