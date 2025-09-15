package com.example.beagle.model;

import java.util.List;

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

    public static final class MessageReadSuccess extends Result {
        private final List<Message> messageList;
        public MessageReadSuccess (List<Message> messageList) {
            this.messageList = messageList;
        }
        public List<Message> getData() {return messageList; }
    }

    public static final class MessageWriteSuccess extends Result {
        private final List<Message> messageList;
        public MessageWriteSuccess (List<Message> messageList) {
            this.messageList = messageList;
        }
        public List<Message> getData() {return messageList; }
    }

    public static final class MessageAISuccess extends Result {
        private final List<Message> messageList;
        public MessageAISuccess (List<Message> messageList) {
            this.messageList = messageList;
        }
        public List<Message> getData() {return messageList; }
    }

    public static final class MessageAPISuccess extends Result {
        private final MessageAPIResponse messageAPIResponse;
        public MessageAPISuccess(MessageAPIResponse messageAPIResponse) {
            this.messageAPIResponse = messageAPIResponse;
        }
        public MessageAPIResponse getData() {return messageAPIResponse; }
    }

    public static final class ConversationSuccess extends Result {
        private final List<Conversation> conversationList;

        public ConversationSuccess(List<Conversation> conversationList) {
            this.conversationList = conversationList;
        }

        public List<Conversation> getData() {return conversationList; }
    }

    public static final class PetSuccess extends Result {
        private final List<Pet> petList;

        public PetSuccess(List<Pet> petList) {
            this.petList = petList;
        }

        public List<Pet> getData() {return petList; }
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
