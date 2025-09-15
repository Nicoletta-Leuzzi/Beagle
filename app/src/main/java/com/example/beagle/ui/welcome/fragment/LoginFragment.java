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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.beagle.R;
import com.example.beagle.model.Result;
import com.example.beagle.repository.user.IUserRepository;
import com.example.beagle.ui.chat.ChatActivity;
import com.example.beagle.ui.welcome.viewmodel.UserViewModel;
import com.example.beagle.ui.welcome.viewmodel.UserViewModelFactory;
import com.example.beagle.util.ServiceLocator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    private EditText etEmail, etPassword;
    private TextInputLayout tilEmail, tilPassword;
    private Button btnLogin, btnGoogle, btnForgot;
    private TextView tvGoRegister;

    private Snackbar currentSnackbar;

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

        setupTypingClearErrors();
        setupListeners();
        setupGoogleSignIn();
    }

    private void initViews(View root) {
        tilEmail    = root.findViewById(R.id.tilEmail);
        tilPassword = root.findViewById(R.id.tilPassword);

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

    // ---------------- EMAIL/PASSWORD ----------------

    private void performEmailLogin() {
        String email = textOf(etEmail);
        String password = textOf(etPassword);

        if (!isEmailValid(email)) {
            if (tilEmail != null) {
                tilEmail.setError(getString(R.string.error_email_login));
                tilEmail.setErrorEnabled(true);
            } else if (etEmail != null) {
                etEmail.setError(getString(R.string.error_email_login));
            }
            etEmail.requestFocus();
            return;
        }
        if (!isPasswordValid(password)) {
            if (tilPassword != null) {
                tilPassword.setError(getString(R.string.error_password_login));
                tilPassword.setErrorEnabled(true);
            } else if (etPassword != null) {
                etPassword.setError(getString(R.string.error_password_login));
            }
            etPassword.requestFocus();
            return;
        }

        clearFieldErrors();
        setLoading(true);

        LiveData<Result> live = userViewModel.getUserMutableLiveData(email, password, /*isUserRegistered=*/true);

        // Evita observer duplicati
        live.removeObservers(getViewLifecycleOwner());

        // Se ha già un valore (es. l'errore del tentativo precedente), ignoriamo SOLO la prima emissione
        final boolean ignoreFirst = (live.getValue() != null);

        Observer<Result> wrapper = new Observer<Result>() {
            boolean first = true;

            @Override public void onChanged(Result result) {
                if (ignoreFirst && first) { first = false; return; }

                setLoading(false);

                if (result instanceof Result.UserSuccess) {
                    userViewModel.setAuthenticationError(false);
                    clearFieldErrors();
                    // ✅ NIENTE controllo isEmailVerified: vai direttamente avanti
                    goNext();
                } else if (result instanceof Result.Error) {
                    userViewModel.setAuthenticationError(true);
                    String msg = ((Result.Error) result).getMessage();
                    showError(msg);
                    if (tilPassword != null) {
                        tilPassword.setError(msg != null ? msg : getString(R.string.error_generic));
                        tilPassword.setErrorEnabled(true);
                    }
                }

                live.removeObserver(this); // one-shot
            }
        };

        live.observe(getViewLifecycleOwner(), wrapper);
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

        LiveData<Result> live = userViewModel.getGoogleUserMutableLiveData(idToken);

        // Evita observer duplicati
        live.removeObservers(getViewLifecycleOwner());

        final boolean ignoreFirst = (live.getValue() != null);

        Observer<Result> wrapper = new Observer<Result>() {
            boolean first = true;

            @Override public void onChanged(Result result) {
                if (ignoreFirst && first) { first = false; return; }

                setLoading(false);

                if (result instanceof Result.UserSuccess) {
                    userViewModel.setAuthenticationError(false);
                    clearFieldErrors();
                    // ✅ NIENTE verifica email: entra direttamente
                    goNext();
                } else if (result instanceof Result.Error) {
                    userViewModel.setAuthenticationError(true);
                    String msg = ((Result.Error) result).getMessage();
                    showError(msg);
                }

                live.removeObserver(this);
            }
        };

        live.observe(getViewLifecycleOwner(), wrapper);
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
        if (currentSnackbar != null) currentSnackbar.dismiss();
        currentSnackbar = Snackbar.make(anchor, message != null ? message : getString(R.string.error_generic), Snackbar.LENGTH_LONG);
        currentSnackbar.show();
    }

    private void goNext() {
        Log.i(TAG, "Navigo a ChatActivity");
        clearFieldErrors();
        if (currentSnackbar != null) {
            currentSnackbar.dismiss();
            currentSnackbar = null;
        }
        startActivity(new Intent(requireContext(), ChatActivity.class));
        requireActivity().finish();
    }

    private void setupTypingClearErrors() {
        SimpleTextWatcher clearWatcher = new SimpleTextWatcher() {
            @Override public void afterTextChanged(android.text.Editable s) {
                if (etEmail != null)    etEmail.setError(null);
                if (etPassword != null) etPassword.setError(null);
                if (tilEmail != null)   { tilEmail.setError(null); tilEmail.setErrorEnabled(false); }
                if (tilPassword != null){ tilPassword.setError(null); tilPassword.setErrorEnabled(false); }
            }
        };
        etEmail.addTextChangedListener(clearWatcher);
        etPassword.addTextChangedListener(clearWatcher);
    }

    private void clearFieldErrors() {
        if (etEmail != null)    etEmail.setError(null);
        if (etPassword != null) etPassword.setError(null);

        if (tilEmail != null) {
            tilEmail.setError(null);
            tilEmail.setErrorEnabled(false);
        }
        if (tilPassword != null) {
            tilPassword.setError(null);
            tilPassword.setErrorEnabled(false);
        }

        if (currentSnackbar != null) {
            currentSnackbar.dismiss();
            currentSnackbar = null;
        }
    }

    // TextWatcher "vuoto" di utilità
    public abstract static class SimpleTextWatcher implements android.text.TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
    }
}
