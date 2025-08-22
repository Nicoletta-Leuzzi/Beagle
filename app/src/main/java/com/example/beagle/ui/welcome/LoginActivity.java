package com.example.beagle.ui.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.beagle.R;
import com.example.beagle.repository.user.IUserRepository;
import com.example.beagle.repository.user.UserRepository;
import com.example.beagle.ui.chat.ChatActivity;
import com.example.beagle.ui.welcome.viewmodel.AuthState;
import com.example.beagle.ui.welcome.viewmodel.UserViewModel;
import com.example.beagle.ui.welcome.viewmodel.UserViewModelFactory;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {

    private UserViewModel vm;

    // UI
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvGoRegister;
    private Button btnGoogle; // opzionale nel layout (se non c’è, verrà null e saltiamo il wiring)

    // One Tap Google
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    private ActivityResultContracts.StartIntentSenderForResult startIntentSenderForResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // --- UI ---
        etEmail      = findViewById(R.id.etEmail);
        etPassword   = findViewById(R.id.etPassword);
        btnLogin     = findViewById(R.id.btnLogin);
        tvGoRegister = findViewById(R.id.tvGoRegister);
        btnGoogle    = findViewById(R.id.btnGoogle); // se non esiste nel layout, resta null

        // --- ViewModel + Factory (come da prof: niente costruttore vuoto) ---
        IUserRepository repository = new UserRepository(); // per ora stub
        UserViewModelFactory factory = new UserViewModelFactory(repository);
        vm = new ViewModelProvider(this, factory).get(UserViewModel.class);

        // --- Observers stato/errore (stile prof) ---
        vm.getState().observe(this, state -> {
            if (state == null) return;
            switch (state) {
                case LOADING:
                    setLoading(true);
                    break;
                case SUCCESS:
                    setLoading(false);
                    goNext();
                    break;
                case ERROR:
                    setLoading(false);
                    // dettaglio errori da getError()
                    break;
                case IDLE:
                default:
                    setLoading(false);
                    break;
            }
        });

        vm.getError().observe(this, err -> {
            if (err != null && !err.trim().isEmpty()) {
                Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
            }
        });

        // --- Email/Password login (stile prof con validate leggere) ---
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String pass  = etPassword.getText() != null ? etPassword.getText().toString() : "";

            if (!isEmailOk(email)) {
                etEmail.setError(getString(R.string.error_email_login));
                return;
            }
            if (!isPasswordOk(pass)) {
                etPassword.setError(getString(R.string.error_password_login));
                return;
            }
            vm.login(email, pass);
        });

        // --- Naviga a Register ---
        tvGoRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );

        // --- Google One Tap (come il prof, ma senza parti news) ---
        // Se nel layout hai un bottone Google, abilitiamo One Tap
        if (btnGoogle != null) {
            setupGoogleOneTap();
            btnGoogle.setOnClickListener(v ->
                    oneTapClient.beginSignIn(signInRequest)
                            .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
                                @Override
                                public void onSuccess(BeginSignInResult result) {
                                    IntentSenderRequest req =
                                            new IntentSenderRequest.Builder(result.getPendingIntent()).build();
                                    activityResultLauncher.launch(req);
                                }
                            })
                            .addOnFailureListener(this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(findViewById(android.R.id.content),
                                            getString(R.string.error_unexpected),
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            })
            );
        }
    }

    private void setupGoogleOneTap() {
        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(
                        BeginSignInRequest.PasswordRequestOptions.builder()
                                .setSupported(true)
                                .build()
                )
                .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                // ⚠️ Usa il client ID web dal tuo google-services (strings.xml: default_web_client_id)
                                .setServerClientId(getString(R.string.default_web_client_id))
                                .setFilterByAuthorizedAccounts(false) // mostra anche account non già autorizzati
                                .build()
                )
                .setAutoSelectEnabled(true) // autoselect se c’è una sola credenziale
                .build();

        startIntentSenderForResult = new ActivityResultContracts.StartIntentSenderForResult();

        activityResultLauncher = registerForActivityResult(startIntentSenderForResult, result -> {
            if (result.getResultCode() == RESULT_OK) {
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                    String idToken = credential.getGoogleIdToken();
                    if (idToken != null) {
                        // 🔄 Passaggio al ViewModel per login con Google
                        // Se hai un metodo vm.loginWithGoogle(idToken), chiamalo qui.
                        // Altrimenti, per ora mostriamo un TODO non bloccante:
                        // vm.loginWithGoogle(idToken);

                        Toast.makeText(this, "Google token ottenuto", Toast.LENGTH_SHORT).show();
                        // TODO: implementa in UserViewModel:
                        // public void loginWithGoogle(String idToken) { ... repo.loginWithGoogle(idToken, callback) ... }
                        // Quando è SUCCESS, lo stesso observer di state porterà a goNext();
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
        });
    }

    private boolean isEmailOk(String email) {
        return !TextUtils.isEmpty(email) && email.contains("@");
        // Oppure valida meglio con Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private boolean isPasswordOk(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }

    private void setLoading(boolean loading) {
        btnLogin.setEnabled(!loading);
        if (btnGoogle != null) btnGoogle.setEnabled(!loading);
        // Se hai una ProgressBar, gestiscila qui:
        // progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void goNext() {
        // Nessuna logica news/categorie: vai diretto alla tua schermata principale
        startActivity(new Intent(this, ChatActivity.class));
        finish();
    }
}
