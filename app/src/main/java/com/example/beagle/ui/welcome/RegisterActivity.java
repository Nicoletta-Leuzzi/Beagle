package com.example.beagle.ui.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beagle.R;
import com.example.beagle.ui.chat.ChatActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Versione minimal che compila:
 * - valida email/password
 * - simula la registrazione
 * - naviga a ChatActivity
 *
 * TODO: sostituire fakeRegister(...) con la chiamata al tuo UserViewModel quando
 *       mi confermi la firma dei metodi (es. register(String,String,String)).
 */
public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText textInputEmail;
    private TextInputEditText textInputPassword;
    private View signupButton;
    private View progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Bind viste (ID coerenti col layout activity_register.xml "minimal")
        textInputEmail = findViewById(R.id.textInputEmail);
        textInputPassword = findViewById(R.id.textInputPassword);
        signupButton = findViewById(R.id.signupButton);
        progressBar = findViewById(R.id.progressBar);

        signupButton.setOnClickListener(v -> {
            String email = getTextOrEmpty(textInputEmail);
            String password = getTextOrEmpty(textInputPassword);

            if (!validateEmail(email)) return;
            if (!validatePassword(password)) return;

            setLoading(true);
            // Per ora: registrazione finta → vai in Chat
            fakeRegister(email, password);
        });
    }

    // ---- Helpers ----

    private void fakeRegister(String email, String password) {
        showSnack(getString(R.string.register_success));
        setLoading(false);
        // Se non hai ancora ChatActivity, sostituisci con finish();
        startActivity(new Intent(this, ChatActivity.class));
        finish();
    }

    private String getTextOrEmpty(TextInputEditText et) {
        return et != null && et.getText() != null ? et.getText().toString().trim() : "";
    }

    private boolean validateEmail(String email) {
        boolean ok = Patterns.EMAIL_ADDRESS.matcher(email).matches();
        if (!ok) {
            if (textInputEmail != null) textInputEmail.setError(getString(R.string.error_email_login));
            showSnack(getString(R.string.error_email_login));
        } else if (textInputEmail != null) {
            textInputEmail.setError(null);
        }
        return ok;
    }

    private boolean validatePassword(String pwd) {
        boolean ok = pwd != null && pwd.length() >= 6;
        if (!ok) {
            if (textInputPassword != null) textInputPassword.setError(getString(R.string.error_password_login));
            showSnack(getString(R.string.error_password_login));
        } else if (textInputPassword != null) {
            textInputPassword.setError(null);
        }
        return ok;
    }

    private void setLoading(boolean loading) {
        if (progressBar != null) progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (signupButton != null) signupButton.setEnabled(!loading);
    }

    private void showSnack(String msg) {
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT).show();
    }
}
