package com.example.beagle.ui.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.beagle.R;
import com.example.beagle.model.Result;
import com.example.beagle.repository.user.IUserRepository;
import com.example.beagle.ui.chat.ChatActivity;
import com.example.beagle.ui.welcome.viewmodel.UserViewModel;
import com.example.beagle.ui.welcome.viewmodel.UserViewModelFactory;
import com.example.beagle.util.ServiceLocator;
import com.google.android.material.snackbar.Snackbar;

// --- Credential Manager + Google ID ---
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;

import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoogle;
    private TextView tvGoRegister;

    private UserViewModel userViewModel;

    // Credential Manager
    private CredentialManager credentialManager;
    private GetSignInWithGoogleOption siwgOption;          // pulsante "Sign in with Google"
    private GetGoogleIdOption googleIdAnyAccountOption;    // fallback: chooser (qualsiasi account)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "onCreate");

        initViews();
        initViewModel();

        // Autologin
        if (userViewModel.getLoggedUser() != null) {
            Log.i(TAG, "Autologin: utente già autenticato, navigo subito");
            goNext();
            return;
        }

        Log.d(TAG, "Nessun utente loggato all'avvio");

        setupListeners();
        setupGoogleSignIn(); // Credential Manager
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoRegister = findViewById(R.id.tvGoRegister);
        btnGoogle = findViewById(R.id.btnGoogle);
    }

    private void initViewModel() {
        IUserRepository repo = ServiceLocator.getInstance().getUserRepository(getApplication());
        userViewModel = new ViewModelProvider(this, new UserViewModelFactory(repo))
                .get(UserViewModel.class);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> performEmailLogin());
        tvGoRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        if (btnGoogle != null) {
            btnGoogle.setOnClickListener(v -> performGoogleLogin());
        }
    }

    private void performEmailLogin() {
        String email = textOf(etEmail);
        String password = textOf(etPassword);

        if (!isEmailValid(email)) {
            etEmail.setError(getString(R.string.error_email_login));
            etEmail.requestFocus();
            return;
        }
        if (!isPasswordValid(password)) {
            etPassword.setError(getString(R.string.error_password_login));
            etPassword.requestFocus();
            return;
        }

        etEmail.setError(null);
        etPassword.setError(null);
        setLoading(true);

        // Cache del LiveData: una sola richiesta/istanza, niente removeObserver
        final androidx.lifecycle.LiveData<Result> loginLiveData =
                userViewModel.getUserMutableLiveData(email, password, /*isUserRegistered=*/true);

        loginLiveData.observe(this, new Observer<Result>() {
                    @Override
                    public void onChanged(Result result) {
                        setLoading(false);

                        if (result instanceof Result.UserSuccess) {
                            userViewModel.setAuthenticationError(false);
                            goNext();
                        } else if (result instanceof Result.Error) {
                            userViewModel.setAuthenticationError(true);
                            showError(((Result.Error) result).getMessage());
                        }
                    }
                });
    }

    // ---------------- GOOGLE SIGN-IN (Credential Manager) ----------------

    private void setupGoogleSignIn() {
        if (btnGoogle == null) {
            Log.d(TAG, "Bottone Google non presente nel layout");
            return;
        }

        try {
            String webClientId = getString(R.string.default_web_client_id);
            if (TextUtils.isEmpty(webClientId) || webClientId.contains("google-services.json")) {
                Log.e(TAG, "default_web_client_id non configurato correttamente");
                btnGoogle.setEnabled(false);
                return;
            }
            Log.d(TAG, "default_web_client_id OK (non placeholder)");

            credentialManager = CredentialManager.create(this);

            // Pulsante ufficiale "Sign in with Google"
            siwgOption = new GetSignInWithGoogleOption.Builder(webClientId)
                    .build();

            // Fallback opzionale: bottom-sheet chooser con QUALSIASI account
            googleIdAnyAccountOption = new GetGoogleIdOption.Builder()
                    .setServerClientId(webClientId)
                    .setFilterByAuthorizedAccounts(false)
                    .build();

        } catch (Exception e) {
            Log.e(TAG, "Errore setup Credential Manager", e);
            btnGoogle.setEnabled(false);
        }
    }

    private void performGoogleLogin() {
        if (credentialManager == null || siwgOption == null) {
            showError("Google Sign-In non disponibile");
            return;
        }

        setLoading(true);

        // Includo sia il pulsante SIWG sia (opzionale) il chooser generico.
        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(siwgOption)
                .addCredentialOption(googleIdAnyAccountOption) // se provider non disponibile, prova chooser
                .build();

        credentialManager.getCredentialAsync(
                this,
                request,
                null,
                ContextCompat.getMainExecutor(this),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse response) {
                        setLoading(false);
                        handleCredentialResponse(response);
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        setLoading(false);
                        if (e instanceof NoCredentialException) {
                            showError("Nessun account Google disponibile sul dispositivo");
                        } else {
                            showError("Accesso Google non completato");
                        }
                        Log.e(TAG, "Google Sign-In error", e);
                    }
                }
        );
    }

    private void handleCredentialResponse(GetCredentialResponse response) {
        try {
            Credential cred = response.getCredential();

            if (cred instanceof CustomCredential) {
                CustomCredential custom = (CustomCredential) cred;

                if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(custom.getType())) {
                    GoogleIdTokenCredential googleCred =
                            GoogleIdTokenCredential.createFrom(custom.getData());

                    String idToken = googleCred.getIdToken();
                    if (!TextUtils.isEmpty(idToken)) {
                        Log.d(TAG, "ID Token ottenuto, procedo con autenticazione");
                        authenticateWithGoogle(idToken);
                        return;
                    }
                }
            }

            Log.e(TAG, "Credenziale non riconosciuta o token mancante");
            showError("Errore: credenziale Google non valida");
        } catch (Exception e) {
            Log.e(TAG, "Errore parsing credenziale Google", e);
            showError("Errore durante l'elaborazione delle credenziali Google");
        }
    }

    private void authenticateWithGoogle(String idToken) {
        setLoading(true);

        // Cache del LiveData: una sola istanza, niente removeObserver
        final androidx.lifecycle.LiveData<Result> googleLiveData =
                userViewModel.getGoogleUserMutableLiveData(idToken);

        googleLiveData.observe(this, new Observer<Result>() {
                    @Override
                    public void onChanged(Result result) {
                        setLoading(false);

                        if (result instanceof Result.UserSuccess) {
                            userViewModel.setAuthenticationError(false);
                            goNext();
                        } else if (result instanceof Result.Error) {
                            userViewModel.setAuthenticationError(true);
                            showError(((Result.Error) result).getMessage());
                        }
                    }
                });
    }

    // ---------------- Util ----------------

    private String textOf(EditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }

    private void setLoading(boolean loading) {
        btnLogin.setEnabled(!loading);
        if (btnGoogle != null) {
            btnGoogle.setEnabled(!loading);
        }
    }

    private void showError(String message) {
        if (findViewById(android.R.id.content) != null) {
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void goNext() {
        Log.i(TAG, "Navigo a ChatActivity");
        Intent intent = new Intent(this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}