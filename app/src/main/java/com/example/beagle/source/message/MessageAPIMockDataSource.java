package com.example.beagle.source.message;


import com.example.beagle.model.Message;
import com.example.beagle.util.JSONParserUtils;

/*
 * CLASSE MOCK PER RICHIEDERE RISPOSTE FINTE DALL' "AI"
 * SI POTREBBE USARE IL FILE DI ESEMPIO DELLA RISPOSTA
 */
public class MessageAPIMockDataSource extends BaseMessageAPIDataSource {

    private final JSONParserUtils jsonParserUtils;

    public MessageAPIMockDataSource(JSONParserUtils jsonParserUtils) {
        this.jsonParserUtils = jsonParserUtils;
    }

    @Override
    public void getReply(long conversationId, int seq) {
        // PRENDE IL FILE DI ESEMPIO E CI FA IL PARSING DA JSON A FILE JAVA
        // E POI LO INVIA INDIETRO CON CALLBACK
        messageCallback.onSuccessFromAPI("REPLY_WIP", conversationId, seq);
    }

}
