package com.example.beagle.ui.welcome.fragment;

import static com.example.beagle.util.Constants.USER_COLLISION_ERROR;
import static com.example.beagle.util.Constants.WEAK_PASSWORD_ERROR;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

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

public class RegisterFragment extends Fragment {

    private UserViewModel userViewModel;

    private TextInputEditText textInputEmail;
    private TextInputEditText textInputPassword;
    private View signupButton;
    private View progressBar;
    private View linkGoLogin;

    public RegisterFragment() { /* empty */ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Come per il login, per rapidità riuso activity_register (puoi duplicarlo come fragment_register).
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(View root, @Nullable Bundle savedInstanceState) {
        // --- Bind UI ---
        textInputEmail    = root.findViewById(R.id.textInputEmail);
        textInputPassword = root.findViewById(R.id.textInputPassword);
        signupButton      = root.findViewById(R.id.signupButton);
        progressBar       = root.findViewById(R.id.progressBar);

        // eventuale TextView "Hai già un account? Accedi" se presente nel layout
        int goLoginId = getResources().getIdentifier("tvGoLogin", "id", requireContext().getPackageName());
        if (goLoginId != 0) {
            linkGoLogin = root.findViewById(goLoginId);
            if (linkGoLogin != null) {
                linkGoLogin.setOnClickListener(v ->
                        NavHostFragment.findNavController(this)
                                .navigate(R.id.action_registerFragment_to_loginFragment));
            }
        }

        // --- VM condivisa con Login ---
        IUserRepository repo = ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(requireActivity(), new UserViewModelFactory(repo))
                .get(UserViewModel.class);
        userViewModel.setAuthenticationError(false);

        // --- Registrazione ---
        signupButton.setOnClickListener(v -> {
            String email = textOf(textInputEmail);
            String pwd   = textOf(textInputPassword);

            if (isEmailOk(email) && isPasswordOk(pwd)) {
                setLoading(true);
                userViewModel.getUserMutableLiveData(email, pwd, /*isUserRegistered=*/false)
                        .observe(getViewLifecycleOwner(), result -> {
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
        startActivity(new Intent(requireContext(), ChatActivity.class));
        requireActivity().finish();
    }

    private String textOf(TextInputEditText et) {
        return (et != null && et.getText() != null) ? et.getText().toString().trim() : "";
    }

    private boolean isEmailOk(String email) {
        boolean ok = email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
        if (textInputEmail != null) textInputEmail.setError(ok ? null : getString(R.string.error_email_login));
        return ok;
    }

    private boolean isPasswordOk(String password) {
        boolean ok = password != null && password.length() >= 6;
        if (textInputPassword != null) textInputPassword.setError(ok ? null : getString(R.string.error_password_login));
        return ok;
    }

    private void setLoading(boolean loading) {
        if (progressBar != null) progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (signupButton != null) signupButton.setEnabled(!loading);
    }

    private void showSnack(String msg) {
        View anchor = getView() != null ? getView() : requireActivity().findViewById(android.R.id.content);
        Snackbar.make(anchor, msg, Snackbar.LENGTH_SHORT).show();
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
