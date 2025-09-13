package com.example.beagle.model;

public abstract class Result {
    private Result() {}

    public boolean isSuccess() {
        return !(this instanceof Error);
    }

    /**
     * Classe che rappresenta un'azione di autenticazione riuscita
     * (login o registrazione) che restituisce un utente (User).
     */
    public static final class UserSuccess extends Result {
        private final User user;

        public UserSuccess(User user) {
            this.user = user;
        }

        public User getData() {
            return user;
        }
    }


    public static final class MessageSuccess extends Result {
        private final MessageAPIResponse messageAPIResponse;
        public MessageSuccess(MessageAPIResponse messageAPIResponse) {
            this.messageAPIResponse = messageAPIResponse;
        }
        public MessageAPIResponse getData() {return messageAPIResponse; }
    }



    /**
     * Classe che rappresenta un errore verificatosi durante
     * l'autenticazione (login o registrazione).
     */
    public static final class Error extends Result {
        private final String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
