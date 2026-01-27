package com.example.filmbox_front;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ¡ESTO ES LO NUEVO Y LO IMPORTANTE!
        // Le dices a esta Activity que use el layout definido en 'activity_register.xml'
        setContentView(R.layout.register);

        // Aquí iría el resto de tu lógica para la pantalla de registro
        // (Por ejemplo, encontrar vistas con findViewById, configurar listeners, etc.)
    }
}