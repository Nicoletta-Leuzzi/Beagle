package com.example.beagle.model;

/**
 * Esito di un'operazione dell'app Beagle.
 * Manteniamo solo i casi che servono all'autenticazione.
 */
public abstract class Result {
    private Result() {}

    /** Operazione riuscita che restituisce un utente. */
    public static final class UserSuccess extends Result {
        private final User user;
        public UserSuccess(User user) { this.user = user; }
        public User getData() { return user; }
    }

    /** Operazione fallita con messaggio. */
    public static final class Error extends Result {
        private final String message;
        public Error(String message) { this.message = message; }
        public String getMessage() { return message; }
    }

    /** Helper: true se non è un errore. */
    public boolean isSuccess() {
        return !(this instanceof Error);
    }
}
