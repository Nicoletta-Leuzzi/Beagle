package com.example.beagle.source.message;



import com.example.beagle.model.ChatCompletionResponse;
import com.example.beagle.util.Constants;
import com.example.beagle.util.JSONParserUtils;

import java.io.IOException;

/*
 * CLASSE MOCK PER RICHIEDERE RISPOSTE FINTE DALL' "AI"
 */

public class MessageAPIMockDataSource extends BaseMessageAPIDataSource {

    private final JSONParserUtils jsonParserUtils;

    public MessageAPIMockDataSource(JSONParserUtils jsonParserUtils) {
        this.jsonParserUtils = jsonParserUtils;
    }

    @Override
    public void getReply(long conversationId, int seq) {
        // PRENDE IL FILE DI ESEMPIO E CI FA IL PARSING DA JSON A FILE JAVA
        ChatCompletionResponse response = null;

        try {
            response = jsonParserUtils.parseJSONResponseWithGson(Constants.SAMPLE_JSON_FILENAME);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // E POI LO INVIA INDIETRO CON CALLBACK
        if (response != null) {
            messageCallback.onSuccessFromAPI(response, conversationId, seq);
        } else {
            messageCallback.onFailureReadFromRemote(new Exception(Constants.API_KEY_ERROR));
        }
    }

}
