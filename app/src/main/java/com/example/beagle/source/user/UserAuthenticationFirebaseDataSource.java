package com.example.beagle.source.user;

import static com.example.beagle.util.Constants.*;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.beagle.model.User;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Gestione autenticazione Firebase (email/password + Google).
 * Allineata al modello del prof.
 */
public class UserAuthenticationFirebaseDataSource extends BaseUserAuthenticationRemoteDataSource {

    private static final String TAG = UserAuthenticationFirebaseDataSource.class.getSimpleName();

    private final FirebaseAuth firebaseAuth;

    public UserAuthenticationFirebaseDataSource() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public User getLoggedUser() {
        FirebaseUser fu = firebaseAuth.getCurrentUser();
        if (fu == null) return null;
        return new User(
                fu.getDisplayName(),         // name
                fu.getEmail(),               // email
                fu.getUid()                  // idToken: usiamo UID come nel prof
        );
    }

    @Override
    public void logout() {
        FirebaseAuth.AuthStateListener listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth fa) {
                if (fa.getCurrentUser() == null) {
                    fa.removeAuthStateListener(this);
                    Log.d(TAG, "User logged out");
                    if (userResponseCallback != null) userResponseCallback.onSuccessLogout();
                }
            }
        };
        firebaseAuth.addAuthStateListener(listener);
        firebaseAuth.signOut();
    }

    @Override
    public void signUp(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser fu = firebaseAuth.getCurrentUser();
                if (fu != null) {
                    if (userResponseCallback != null) {
                        userResponseCallback.onSuccessFromAuthentication(
                                new User(fu.getDisplayName(), email, fu.getUid())
                        );
                    }
                } else {
                    if (userResponseCallback != null)
                        userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                }
            } else {
                if (userResponseCallback != null)
                    userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
            }
        });
    }

    @Override
    public void signIn(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser fu = firebaseAuth.getCurrentUser();
                if (fu != null) {
                    if (userResponseCallback != null) {
                        userResponseCallback.onSuccessFromAuthentication(
                                new User(fu.getDisplayName(), email, fu.getUid())
                        );
                    }
                } else {
                    if (userResponseCallback != null)
                        userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                }
            } else {
                if (userResponseCallback != null)
                    userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
            }
        });
    }

    @Override
    public void signInWithGoogle(String idToken) {
        if (idToken == null) {
            if (userResponseCallback != null)
                userResponseCallback.onFailureFromAuthentication(UNEXPECTED_ERROR);
            return;
        }

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "signInWithCredential:success");
                FirebaseUser fu = firebaseAuth.getCurrentUser();
                if (fu != null) {
                    if (userResponseCallback != null) {
                        userResponseCallback.onSuccessFromAuthentication(
                                new User(fu.getDisplayName(), fu.getEmail(), fu.getUid())
                        );
                    }
                } else {
                    if (userResponseCallback != null)
                        userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                }
            } else {
                Log.w(TAG, "signInWithCredential:failure", task.getException());
                if (userResponseCallback != null)
                    userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
            }
        });
    }

    /** Mappa eccezioni Firebase in codici d'errore costanti (stile prof). */
    private String getErrorMessage(Exception e) {
        if (e instanceof FirebaseAuthWeakPasswordException) {
            return WEAK_PASSWORD_ERROR;
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            return INVALID_CREDENTIALS_ERROR;
        } else if (e instanceof FirebaseAuthInvalidUserException) {
            return INVALID_USER_ERROR;
        } else if (e instanceof FirebaseAuthUserCollisionException) {
            return USER_COLLISION_ERROR;
        }
        return UNEXPECTED_ERROR;
    }
}
