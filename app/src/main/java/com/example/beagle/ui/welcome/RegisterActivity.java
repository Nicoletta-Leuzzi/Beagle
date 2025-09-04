package com.example.beagle.ui.welcome;

import static com.example.beagle.util.Constants.USER_COLLISION_ERROR;
import static com.example.beagle.util.Constants.WEAK_PASSWORD_ERROR;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.beagle.R;
import com.example.beagle.model.Result;
import com.example.beagle.model.User;
import com.example.beagle.repository.user.IUserRepository;
import com.example.beagle.ui.chat.ChatActivity;
import com.example.beagle.ui.welcome.viewmodel.UserViewModel;
import com.example.beagle.ui.welcome.viewmodel.UserViewModelFactory;
import com.example.beagle.util.ServiceLocator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

/** RegisterActivity — versione “prof-like” solo per autenticazione. */
public class RegisterActivity extends AppCompatActivity {

    private UserViewModel userViewModel;

    private TextInputEditText textInputEmail;
    private TextInputEditText textInputPassword;
    private View signupButton;
    private View progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // --- Bind UI ---
        textInputEmail    = findViewById(R.id.textInputEmail);
        textInputPassword = findViewById(R.id.textInputPassword);
        signupButton      = findViewById(R.id.signupButton);
        progressBar       = findViewById(R.id.progressBar);

        // --- VM stile prof: ServiceLocator -> Repo -> Factory ---
        IUserRepository repo =
                ServiceLocator.getInstance().getUserRepository(getApplication());
        userViewModel = new ViewModelProvider(
                this,
                new UserViewModelFactory(repo)
        ).get(UserViewModel.class);
        userViewModel.setAuthenticationError(false);

        // --- Registrazione ---
        signupButton.setOnClickListener(v -> {
            String email = textOf(textInputEmail);
            String pwd   = textOf(textInputPassword);

            if (isEmailOk(email) && isPasswordOk(pwd)) {
                setLoading(true);
                userViewModel.getUserMutableLiveData(email, pwd, /*isUserRegistered=*/false)
                        .observe(this, result -> {
                            setLoading(false);
                            if (result instanceof Result.UserSuccess) {
                                User user = ((Result.UserSuccess) result).getData();
                                userViewModel.setAuthenticationError(false);
                                goNext();
                            } else if (result instanceof Result.Error) {
                                userViewModel.setAuthenticationError(true);
                                showSnack(getErrorMessage(((Result.Error) result).getMessage()));
                            }
                        });
            } else {
                userViewModel.setAuthenticationError(true);
                showSnack(getString(R.string.error_email_login));
            }
        });
    }

    // ----------------- Helpers -----------------

    private void goNext() {
        startActivity(new Intent(this, ChatActivity.class));
        finish();
    }

    private String textOf(TextInputEditText et) {
        return (et != null && et.getText() != null) ? et.getText().toString().trim() : "";
    }

    private boolean isEmailOk(String email) {
        boolean ok = email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
        if (!ok && textInputEmail != null) {
            textInputEmail.setError(getString(R.string.error_email_login));
        } else if (textInputEmail != null) {
            textInputEmail.setError(null);
        }
        return ok;
    }

    private boolean isPasswordOk(String password) {
        boolean ok = password != null && password.length() >= 6;
        if (!ok && textInputPassword != null) {
            textInputPassword.setError(getString(R.string.error_password_login));
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

    /** Mappa stringhe errore del repo/DS Firebase alle risorse UI. */
    private String getErrorMessage(String type) {
        if (WEAK_PASSWORD_ERROR.equals(type)) {
            return getString(R.string.error_password_login);
        } else if (USER_COLLISION_ERROR.equals(type)) {
            return getString(R.string.error_collision_user);
        } else {
            return getString(R.string.error_unexpected);
        }
    }
}
