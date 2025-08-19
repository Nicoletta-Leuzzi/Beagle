package com.example.beagle.ui.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.beagle.R;
import com.example.beagle.ui.chat.ChatActivity;
import com.example.beagle.ui.welcome.viewmodel.AuthState;
import com.example.beagle.ui.welcome.viewmodel.UserViewModel;

public class LoginActivity extends AppCompatActivity {

    private UserViewModel vm;

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvGoRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // UI
        etEmail      = findViewById(R.id.etEmail);
        etPassword   = findViewById(R.id.etPassword);
        btnLogin     = findViewById(R.id.btnLogin);
        tvGoRegister = findViewById(R.id.tvGoRegister);

        // ViewModel senza factory (il nostro VM stub ha costruttore vuoto)
        vm = new ViewModelProvider(this).get(UserViewModel.class);

        // Osserva lo stato
        vm.getState().observe(this, state -> {
            if (state == null) return;
            switch (state) {
                case LOADING:
                    setLoading(true);
                    break;
                case SUCCESS:
                    setLoading(false);
                    goHome();
                    break;
                case ERROR:
                    setLoading(false);
                    Toast.makeText(this, "Errore di autenticazione", Toast.LENGTH_SHORT).show();
                    break;
                case IDLE:
                default:
                    setLoading(false);
                    break;
            }
        });

        // Vai a registrazione
        tvGoRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );

        // Login email/password (usa lo stub vm.login)
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass  = etPassword.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                Toast.makeText(this, "Inserisci email e password", Toast.LENGTH_SHORT).show();
                return;
            }
            vm.login(email, pass); // negli stub porterà subito a SUCCESS
        });
    }

    private void setLoading(boolean loading) {
        btnLogin.setEnabled(!loading);
        // se hai una ProgressBar nel layout, gestiscila qui
        // progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void goHome() {
        // TODO: sostituisci con la tua HomeActivity quando pronta
        Toast.makeText(this, "Login OK", Toast.LENGTH_SHORT).show();

        // TODO: rifare usando nav graph quando diventerà una fragment
        startActivity(new Intent(this, ChatActivity.class));

        finish();
    }
}
