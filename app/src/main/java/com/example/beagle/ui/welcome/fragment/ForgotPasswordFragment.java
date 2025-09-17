package com.example.beagle.ui.welcome.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.beagle.R;
import com.example.beagle.model.Result;
import com.example.beagle.repository.user.IUserRepository;
import com.example.beagle.ui.welcome.viewmodel.UserViewModel;
import com.example.beagle.ui.welcome.viewmodel.UserViewModelFactory;
import com.example.beagle.util.ServiceLocator;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.button.MaterialButton;

public class ForgotPasswordFragment extends Fragment {

    public ForgotPasswordFragment() {
        super(R.layout.fragment_forgot_password);
    }

    private TextInputLayout tilEmail;
    private TextInputEditText etEmail;
    private MaterialButton btnReset;
    private View progress;

    private UserViewModel userViewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tilEmail = view.findViewById(R.id.tilEmail);
        etEmail = view.findViewById(R.id.etEmail);
        btnReset = view.findViewById(R.id.btnReset);
        MaterialButton linkBackLogin = view.findViewById(R.id.linkBackLogin);
        progress = view.findViewById(R.id.progress);

        // init ViewModel via ServiceLocator/IUserRepository
        IUserRepository repo = ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(requireActivity(), new UserViewModelFactory(repo))
                .get(UserViewModel.class);

        // Validazione live email
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnReset.setEnabled(isValidEmail(s.toString().trim()));
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        btnReset.setEnabled(false);

        btnReset.setOnClickListener(v -> onResetClick());
        linkBackLogin.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp()
        );
    }

    private void onResetClick() {
        tilEmail.setError(null);
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";

        if (!isValidEmail(email)) {
            tilEmail.setError(getString(R.string.error_invalid_email));
            return;
        }

        setLoading(true);

        // chiama il ViewModel (che delega al repository)
        userViewModel.resetPassword(email).observe(getViewLifecycleOwner(), result -> {
            setLoading(false);
            if (result instanceof Result.UserSuccess) {
                showMailSentDialog(email);
            } else if (result instanceof Result.Error) {
                String msg = ((Result.Error) result).getMessage();
                Snackbar.make(requireView(),
                        msg != null ? msg : getString(R.string.error_generic),
                        Snackbar.LENGTH_LONG
                ).show();
            }
        });
    }

    private void showMailSentDialog(String email) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.forgot_title))
                .setMessage(getString(R.string.forgot_email_sent, email))
                .setPositiveButton("Apri Mail", (d, w) -> {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try { startActivity(intent); } catch (Exception ignored) {}
                    NavHostFragment.findNavController(this).navigateUp();
                })
                .setNegativeButton("Ok", (d, w) -> {
                    d.dismiss();
                    NavHostFragment.findNavController(this).navigateUp();
                })
                .show();
    }

    private void setLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnReset.setEnabled(!loading && isValidEmail(etEmail.getText() != null ? etEmail.getText().toString().trim() : ""));
        etEmail.setEnabled(!loading);
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
