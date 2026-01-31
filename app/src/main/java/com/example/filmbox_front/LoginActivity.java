package com.example.filmbox_front;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;

    private static final String PREFS_NAME = "FilmBoxPrefs";
    private static final String TOKEN_KEY = "SESSION_TOKEN"; // clave exacta que lee el fragment
    private static final String USERNAME_KEY = "USERNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        etUsername = findViewById(R.id.name_input);
        etPassword = findViewById(R.id.password_input);
        btnLogin = findViewById(R.id.login_button);
        tvRegisterLink = findViewById(R.id.has_account_text);

        tvRegisterLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Ingresa usuario y contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            LoginRequest loginRequest = new LoginRequest(username, password);
            ApiService apiService = RetrofitClient.getApiService();

            apiService.loginUser(loginRequest).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = response.body();
                        String token = loginResponse.getToken();

                        // Guardar token con clave correcta
                        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
                        editor.putString(TOKEN_KEY, token);      // sesión
                        editor.putString(USERNAME_KEY, loginResponse.getUsername()); // usuario
                        editor.apply();

                        Log.d("LoginActivity", "Token guardado: " + token);

                        Toast.makeText(LoginActivity.this, loginResponse.getDetail(), Toast.LENGTH_SHORT).show();

                        // Ir a pantalla principal
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        String errorMessage = "Error en el login: Credenciales inválidas";
                        if (response.errorBody() != null) {
                            errorMessage += " - " + response.code();
                        }
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        Log.e("LoginActivity", errorMessage);
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("LoginActivity", "Error de red", t);
                }
            });
        });
    }
}
