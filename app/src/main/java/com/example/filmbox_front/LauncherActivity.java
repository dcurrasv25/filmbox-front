package com.example.filmbox_front;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        boolean logged = prefs.getBoolean("logged", false);

        Intent intent;
        if (logged) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, RegisterActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
