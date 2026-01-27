package com.example.filmbox_front;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Aquí podrías obtener estos valores desde login o SharedPreferences
    private String sessionToken = "TU_TOKEN_DE_PRUEBA";
    private String username = "UsuarioPrueba";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        bottomNav.setItemIconTintList(null);
        bottomNav.setItemActiveIndicatorEnabled(false);
        bottomNav.setItemRippleColor(ColorStateList.valueOf(Color.TRANSPARENT));

        getWindow().setNavigationBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
                return true;

            } else if (item.getItemId() == R.id.nav_search) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SearchFragment())
                        .commit();
                return true;

            } else if (item.getItemId() == R.id.nav_profile) {
                // Pasar token y username al ProfileFragment
                Bundle args = new Bundle();
                args.putString("SESSION_TOKEN", sessionToken);
                args.putString("USERNAME", username);

                ProfileFragment profileFragment = new ProfileFragment();
                profileFragment.setArguments(args);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, profileFragment)
                        .commit();
                return true;
            }
            return false;
        });
    }
}

