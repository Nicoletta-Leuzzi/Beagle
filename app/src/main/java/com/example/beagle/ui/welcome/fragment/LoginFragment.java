package com.example.beagle.ui.welcome.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.beagle.R;
import com.example.beagle.model.Result;
import com.example.beagle.repository.user.IUserRepository;
import com.example.beagle.ui.chat.ChatActivity;
import com.example.beagle.ui.welcome.viewmodel.UserViewModel;
import com.example.beagle.ui.welcome.viewmodel.UserViewModelFactory;
import com.example.beagle.util.ServiceLocator;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoogle, btnForgot;
    private TextView tvGoRegister;

    private UserViewModel userViewModel;

    // Credential Manager
    private CredentialManager credentialManager;
    private GetSignInWithGoogleOption siwgOption;
    private GetGoogleIdOption googleIdAnyAccountOption;

    public LoginFragment() { /* empty */ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        initViews(view);
        initViewModel();

        // Autologin
        if (userViewModel.getLoggedUser() != null) {
            Log.i(TAG, "Autologin: utente già autenticato, navigo subito");
            goNext();
            return;
        }

        setupListeners();
        setupGoogleSignIn();
    }

    private void initViews(View root) {
        etEmail      = root.findViewById(R.id.etEmail);
        etPassword   = root.findViewById(R.id.etPassword);
        btnLogin     = root.findViewById(R.id.btnLogin);
        btnGoogle    = root.findViewById(R.id.btnGoogle);
        btnForgot    = root.findViewById(R.id.btnForgot);
        tvGoRegister = root.findViewById(R.id.tvGoRegister);
    }

    private void initViewModel() {
        IUserRepository repo = ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(requireActivity(), new UserViewModelFactory(repo))
                .get(UserViewModel.class); // VM condiviso tra Login/Register
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> performEmailLogin());

        tvGoRegister.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_loginFragment_to_registerFragment));

        if (btnForgot != null) {
            btnForgot.setOnClickListener(v ->
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_loginFragment_to_forgotPasswordFragment));
        }

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

        userViewModel.getUserMutableLiveData(email, password, /*isUserRegistered=*/true)
                .observe(getViewLifecycleOwner(), result -> {
                    setLoading(false);
                    if (result instanceof Result.UserSuccess) {
                        userViewModel.setAuthenticationError(false);
                        if (!isEmailVerified()) {
                            showVerifyDialog(); // <-- blocca qui se non verificata
                            return;
                        }
                        goNext();
                    } else if (result instanceof Result.Error) {
                        userViewModel.setAuthenticationError(true);
                        showError(((Result.Error) result).getMessage());
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

            credentialManager = CredentialManager.create(requireContext());

            siwgOption = new GetSignInWithGoogleOption.Builder(webClientId).build();
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

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(siwgOption)
                .addCredentialOption(googleIdAnyAccountOption)
                .build();

        credentialManager.getCredentialAsync(
                requireActivity(),
                request,
                null,
                ContextCompat.getMainExecutor(requireContext()),
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

        userViewModel.getGoogleUserMutableLiveData(idToken)
                .observe(getViewLifecycleOwner(), result -> {
                    setLoading(false);
                    if (result instanceof Result.UserSuccess) {
                        userViewModel.setAuthenticationError(false);
                        // Con Google, spesso l’email è già verificata, ma controlliamo lo stesso
                        if (!isEmailVerified()) {
                            showVerifyDialog();
                            return;
                        }
                        goNext();
                    } else if (result instanceof Result.Error) {
                        userViewModel.setAuthenticationError(true);
                        showError(((Result.Error) result).getMessage());
                    }
                });
    }

    // ---------------- Email Verification helpers ----------------
    private boolean isEmailVerified() {
        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
        return current != null && current.isEmailVerified();
    }

    private void showVerifyDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(getStringSafe(R.string.verify_title, "Verifica email"))
                .setMessage(getStringSafe(R.string.verify_needed, "Devi verificare la tua email per continuare."))
                .setPositiveButton(getStringSafe(R.string.verify_resend, "Invia di nuovo"), (d, w) -> {
                    userViewModel.resendEmailVerification().observe(getViewLifecycleOwner(), r -> {
                        if (r instanceof Result.UserSuccess) {
                            String email = FirebaseAuth.getInstance().getCurrentUser() != null
                                    ? FirebaseAuth.getInstance().getCurrentUser().getEmail()
                                    : "";
                            Snackbar.make(requireView(),
                                    getStringSafe(R.string.verify_sent, "Ti abbiamo inviato una email di verifica a %1$s.", email),
                                    Snackbar.LENGTH_LONG).show();
                        } else if (r instanceof Result.Error) {
                            String msg = ((Result.Error) r).getMessage();
                            Snackbar.make(requireView(),
                                    msg != null ? msg : getString(R.string.error_generic),
                                    Snackbar.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton(getStringSafe(R.string.open_mail, "Apri Mail"), (d, w) -> {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try { startActivity(intent); } catch (Exception ignored) {}
                })
                .show();
    }

    // Se una stringa non esiste, usa il fallback literal passato.
    private String getStringSafe(int resId, String fallback, Object... formatArgs) {
        try {
            return formatArgs != null && formatArgs.length > 0
                    ? getString(resId, formatArgs)
                    : getString(resId);
        } catch (Exception e) {
            if (formatArgs != null && formatArgs.length > 0) {
                try { return String.format(fallback, formatArgs); } catch (Exception ignore) {}
            }
            return fallback;
        }
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
        if (btnGoogle != null) btnGoogle.setEnabled(!loading);
        if (btnForgot != null) btnForgot.setEnabled(!loading);
    }

    private void showError(String message) {
        View anchor = getView() != null ? getView() : requireActivity().findViewById(android.R.id.content);
        Snackbar.make(anchor, message != null ? message : getString(R.string.error_generic), Snackbar.LENGTH_LONG).show();
    }

    private void goNext() {
        Log.i(TAG, "Navigo a ChatActivity");
        startActivity(new Intent(requireContext(), ChatActivity.class));
        requireActivity().finish();
    }
}
