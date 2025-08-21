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

import com.example.beagle.R;
import com.example.beagle.ui.chat.ChatActivity;
import com.example.beagle.ui.welcome.viewmodel.AuthState;
import com.example.beagle.ui.welcome.viewmodel.UserViewModel;
import com.example.beagle.ui.welcome.viewmodel.UserViewModelFactory;

import com.example.beagle.repository.user.IUserRepository;
import com.example.beagle.repository.user.UserRepository;

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

        // ViewModel CON factory (necessaria perché UserViewModel richiede IUserRepository)
        IUserRepository repository = new UserRepository(); // per ora usa gli stub
        UserViewModelFactory factory = new UserViewModelFactory(repository);
        vm = new ViewModelProvider(this, factory).get(UserViewModel.class);

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
                    // il dettaglio arriva da getError()
                    break;
                case IDLE:
                default:
                    setLoading(false);
                    break;
            }
        });

        // Osserva eventuali errori dal ViewModel
        vm.getError().observe(this, err -> {
            if (err != null && !err.trim().isEmpty()) {
                Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
            }
        });

        // Vai a registrazione
        tvGoRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );

        // Login email/password
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass  = etPassword.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                Toast.makeText(this, "Inserisci email e password", Toast.LENGTH_SHORT).show();
                return;
            }
            vm.login(email, pass);
        });
    }

    private void setLoading(boolean loading) {
        btnLogin.setEnabled(!loading);
        // TODO: se hai una ProgressBar nel layout, gestiscila qui
        // progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void goHome() {
        Toast.makeText(this, "Login OK", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, ChatActivity.class));
        finish();
    }
}
