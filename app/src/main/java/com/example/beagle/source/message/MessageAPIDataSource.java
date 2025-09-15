package com.example.beagle.source.message;

import com.example.beagle.model.Message;

/*
 * CLASSE PER RICHIEDERE RISPOSTA ALL'AI
 */
public class MessageAPIDataSource extends BaseMessageAPIDataSource{


    private final String apiKey;

    public MessageAPIDataSource(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public void getReply(long conversationId, int seq) {
        // DOVREBBE RITORNARE AL CALLBACK LA RISPOSTA DELL'AI
        // DA ELABORARE IN ALTRE CLASSI (PENSO)
    }

}
