package com.example.beagle.service;

import com.example.beagle.model.APIMessage;
import com.example.beagle.model.ChatCompletionRequest;
import com.example.beagle.model.Message;
import com.example.beagle.model.Pet;
import com.example.beagle.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class ChatCompletionRequestBuilder {

    public ChatCompletionRequest build(Pet pet, List<Message> history) {
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel(Constants.MODEL);
        request.setMessages(buildMessages(pet, history));
        return request;
    }

    private List<APIMessage> buildMessages(Pet pet, List<Message> history) {
        List<APIMessage> messages = new ArrayList<>();
        messages.add(buildSystemMessage(pet));
        for (Message message : history) {
            messages.add(mapMessage(message));
        }
        return messages;
    }

    private APIMessage buildSystemMessage(Pet pet) {
        APIMessage system = new APIMessage();
        system.setRole("system");
        system.setContent(
                "You are Beagle, a veterinary assistant supporting the owner of "
                        + pet.getName()
                        + ", a " + pet.getSpeciesString()
                        + " breed " + safeValue(pet.getBreed())
                        + ", approximately " + safeValue(pet.getAge()) + " months old. "
                        + "Provide concise, empathetic guidance tailored to this pet."
        );
        return system;
    }

    private APIMessage mapMessage(Message message) {
        APIMessage apiMessage = new APIMessage();
        apiMessage.setRole(message.isFromUser() ? "user" : "assistant");
        apiMessage.setContent(message.getContent());
        return apiMessage;
    }

    private String safeValue(String value) {
        return value != null && !value.isEmpty() ? value : "unknown";
    }
}
