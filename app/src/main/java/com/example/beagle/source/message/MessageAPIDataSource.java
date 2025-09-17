package com.example.beagle.source.message;

import static com.example.beagle.util.Constants.RETROFIT_ERROR;

import androidx.annotation.NonNull;

import com.example.beagle.database.ConversationDAO;
import com.example.beagle.database.DataRoomDatabase;
import com.example.beagle.database.PetDAO;
import com.example.beagle.model.ChatCompletionRequest;
import com.example.beagle.model.ChatCompletionResponse;
import com.example.beagle.model.Conversation;
import com.example.beagle.model.Message;
import com.example.beagle.database.MessageDAO;
import com.example.beagle.model.Pet;
import com.example.beagle.service.ChatCompletionRequestBuilder;
import com.example.beagle.service.ResponseAPIService;
import com.example.beagle.util.ServiceLocator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * CLASSE PER RICHIEDERE RISPOSTA ALL'AI
 */
public class MessageAPIDataSource extends BaseMessageAPIDataSource{

    private final MessageDAO messageDAO;
    private final ConversationDAO conversationDAO;
    private final PetDAO petDAO;
    private final ChatCompletionRequestBuilder requestBuilder;
    private ResponseAPIService responseAPIService;
    private final String apiKey;

    public MessageAPIDataSource(DataRoomDatabase database, String apiKey) {
        this.messageDAO = database.messageDao();
        this.conversationDAO = database.conversationDao();
        this.petDAO = database.petDao();
        this.requestBuilder = new ChatCompletionRequestBuilder();
        this.responseAPIService = ServiceLocator.getInstance().getResponseAPIService();
        this.apiKey = apiKey;
    }

    @Override
    public void getReply(long conversationId, int seq) {

        //devo ritornare ChatCompletionResponse, conversationId e seq tramite callback

        DataRoomDatabase.databaseWriteExecutor.execute(() -> {
            // 1. Recupera cronologia ordinata
            List<Message> history = messageDAO.getMessages(conversationId);

            // 2. Recupera conversazione → petId
            Conversation conversation = conversationDAO.getConversationById(conversationId);
            if (conversation == null) {
                messageCallback.onFailureReadFromRemote(
                        new IllegalStateException("Conversation " + conversationId + " not found"));
                return;
            }

            // 3. Recupera il pet associato
            Pet pet = petDAO.getPetById(conversation.getPetId());
            if (pet == null) {
                messageCallback.onFailureReadFromRemote(
                        new IllegalStateException("Pet " + conversation.getPetId() + " not found"));
                return;
            }

            // 4. Costruisci la request
            ChatCompletionRequest request = requestBuilder.build(pet, history);

            // 5. Chiamata Retrofit
            Call<ChatCompletionResponse> call =
                    responseAPIService.getChatCompletionResponse(request, "Bearer " + apiKey);

            call.enqueue(new Callback<ChatCompletionResponse>() {
                @Override
                public void onResponse(@NonNull Call<ChatCompletionResponse> call,
                                       @NonNull Response<ChatCompletionResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        messageCallback.onSuccessFromAPI(response.body(), conversationId, seq);
                    } else {
                        messageCallback.onFailureReadFromRemote(
                                new Exception("HTTP " + response.code()));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ChatCompletionResponse> call, @NonNull Throwable t) {
                    messageCallback.onFailureReadFromRemote(new Exception(RETROFIT_ERROR));
                }
            });
        });
    }
}
