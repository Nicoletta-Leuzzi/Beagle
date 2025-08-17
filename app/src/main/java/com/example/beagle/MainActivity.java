package com.example.beagle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beagle.ui.welcome.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Se nel layout esiste un bottone con id btnLogout, usalo per tornare al Login.
        Button btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                // Nessun AuthStore negli stub: facciamo solo "torna al login".
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            });
        }

        // Se vuoi forzare subito la schermata di login durante lo sviluppo, scommenta:
        // startActivity(new Intent(this, LoginActivity.class));
        // finish();
    }
}
