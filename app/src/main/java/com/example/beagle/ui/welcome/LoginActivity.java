package com.example.beagle.ui.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoogle;
    private TextView tvGoRegister;

    private UserViewModel userViewModel;

    // Google One Tap (opzionale)
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private ActivityResultLauncher<IntentSenderRequest> oneTapLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // --- UI ---
        etEmail      = findViewById(R.id.etEmail);
        etPassword   = findViewById(R.id.etPassword);
        btnLogin     = findViewById(R.id.btnLogin);
        tvGoRegister = findViewById(R.id.tvGoRegister);
        btnGoogle    = findViewById(R.id.btnGoogle); // può essere assente nel layout

        // --- VM stile prof: ServiceLocator -> Repo -> Factory ---
        IUserRepository repo =
                ServiceLocator.getInstance().getUserRepository(getApplication());
        userViewModel = new ViewModelProvider(
                this,
                new UserViewModelFactory(repo)
        ).get(UserViewModel.class);

        // Autologin se già autenticato (opzionale)
        if (userViewModel.getLoggedUser() != null) {
            goNext();
            return;
        }

        // --- Login email/password ---
        btnLogin.setOnClickListener(v -> {
            String email = textOf(etEmail);
            String pass  = textOf(etPassword);

            if (!isEmailOk(email)) {
                etEmail.setError(getString(R.string.error_email_login));
                return;
            }
            if (!isPasswordOk(pass)) {
                etPassword.setError(getString(R.string.error_password_login));
                return;
            }

            setLoading(true);
            userViewModel.getUserMutableLiveData(email, pass, /*isUserRegistered=*/true)
                    .observe(this, result -> {
                        setLoading(false);
                        if (result instanceof Result.UserSuccess) {
                            User u = ((Result.UserSuccess) result).getData();
                            userViewModel.setAuthenticationError(false);
                            goNext();
                        } else if (result instanceof Result.Error) {
                            userViewModel.setAuthenticationError(true);
                            Toast.makeText(this,
                                    ((Result.Error) result).getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // --- Navigazione a Register ---
        tvGoRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );

        // --- Google One Tap (se presente il bottone) ---
        if (btnGoogle != null) {
            setupGoogleOneTap();
            btnGoogle.setOnClickListener(v ->
                    oneTapClient.beginSignIn(signInRequest)
                            .addOnSuccessListener(this, (BeginSignInResult res) -> {
                                IntentSenderRequest req =
                                        new IntentSenderRequest.Builder(res.getPendingIntent()).build();
                                oneTapLauncher.launch(req);
                            })
                            .addOnFailureListener(this, e ->
                                    Snackbar.make(findViewById(android.R.id.content),
                                            getString(R.string.error_unexpected),
                                            Snackbar.LENGTH_SHORT).show()
                            )
            );
        }
    }

    // ----------------- One Tap -----------------
    private void setupGoogleOneTap() {
        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(
                        BeginSignInRequest.PasswordRequestOptions.builder()
                                .setSupported(true).build())
                .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId(getString(R.string.default_web_client_id))
                                .setFilterByAuthorizedAccounts(false)
                                .build()
                )
                .setAutoSelectEnabled(true)
                .build();

        oneTapLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        try {
                            SignInCredential cred =
                                    oneTapClient.getSignInCredentialFromIntent(result.getData());
                            String idToken = cred.getGoogleIdToken();
                            if (idToken != null) {
                                setLoading(true);
                                userViewModel.getGoogleUserMutableLiveData(idToken)
                                        .observe(this, res -> {
                                            setLoading(false);
                                            if (res instanceof Result.UserSuccess) {
                                                userViewModel.setAuthenticationError(false);
                                                goNext();
                                            } else if (res instanceof Result.Error) {
                                                userViewModel.setAuthenticationError(true);
                                                Snackbar.make(findViewById(android.R.id.content),
                                                        ((Result.Error) res).getMessage(),
                                                        Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Snackbar.make(findViewById(android.R.id.content),
                                        getString(R.string.error_unexpected),
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        } catch (ApiException e) {
                            Snackbar.make(findViewById(android.R.id.content),
                                    getString(R.string.error_unexpected),
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
    // -------------------------------------------

    // ----------------- Helpers -----------------
    private String textOf(EditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private boolean isEmailOk(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordOk(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }

    private void setLoading(boolean loading) {
        btnLogin.setEnabled(!loading);
        if (btnGoogle != null) btnGoogle.setEnabled(!loading);
        // progressBar.setVisibility(loading ? View.VISIBLE : View.GONE); // se la aggiungi
    }

    private void goNext() {
        startActivity(new Intent(this, ChatActivity.class));
        finish();
    }
}
