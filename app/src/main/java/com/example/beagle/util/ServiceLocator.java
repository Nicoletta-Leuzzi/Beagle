package com.example.beagle.util;

import android.app.Application;
import android.content.Context;

import com.example.beagle.R;
import com.example.beagle.database.DataRoomDatabase;
import com.example.beagle.repository.conversation.ConversationRepository;
import com.example.beagle.repository.message.MessageRepository;
import com.example.beagle.repository.pet.PetRepository;
import com.example.beagle.repository.user.IUserRepository;
import com.example.beagle.repository.user.UserRepository;
import com.example.beagle.service.ResponseAPIService;
import com.example.beagle.source.conversation.BaseConversationLocalDataSource;
import com.example.beagle.source.conversation.BaseConversationRemoteDataSource;
import com.example.beagle.source.conversation.ConversationFirebaseDataSource;
import com.example.beagle.source.conversation.ConversationLocalDataSource;
import com.example.beagle.source.message.BaseMessageAPIDataSource;
import com.example.beagle.source.message.BaseMessageLocalDataSource;
import com.example.beagle.source.message.BaseMessageRemoteDataSource;
import com.example.beagle.source.message.MessageAPIDataSource;
import com.example.beagle.source.message.MessageAPIMockDataSource;
import com.example.beagle.source.message.MessageLocalDataSource;
import com.example.beagle.source.message.MessageFirebaseDataSource;
import com.example.beagle.source.pet.BasePetLocalDataSource;
import com.example.beagle.source.pet.BasePetRemoteDataSource;
import com.example.beagle.source.pet.PetFirebaseDataSource;
import com.example.beagle.source.pet.PetLocalDataSource;
import com.example.beagle.source.user.BaseUserAuthenticationRemoteDataSource;
import com.example.beagle.source.user.UserAuthenticationFirebaseDataSource;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ServiceLocator – versione per AUTH (login/registrazione).
 * - Singleton: getInstance()
 * - Fornisce il repository utente tramite getUserRepository(Application)
 */
public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE;

    private IUserRepository userRepository;

    private ServiceLocator() { }

    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized (ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }

    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .build();
                return chain.proceed(request);
            })
            .build();

    @Deprecated
    public static ServiceLocator getInstance(Context ignored) {
        return getInstance();
    }


    // Restituisce il repository utente (solo autenticazione)
    public IUserRepository getUserRepository(Application application) {
        if (userRepository == null) {
            BaseUserAuthenticationRemoteDataSource authDs = new UserAuthenticationFirebaseDataSource();
            userRepository = new UserRepository(authDs);
        }
        return userRepository;
    }


    public DataRoomDatabase getDao(Application application) {
        return DataRoomDatabase.getDatabase(application);
    }

    public MessageRepository getMessageRepository(Application application, boolean debug_mode) {
        BaseMessageRemoteDataSource messageRemoteDataSource;
        BaseMessageLocalDataSource messageLocalDataSource;
        BaseMessageAPIDataSource messageAPIDataSource;


        messageRemoteDataSource = new MessageFirebaseDataSource();
        messageLocalDataSource = new MessageLocalDataSource(getDao(application));

        if (debug_mode) {
            JSONParserUtils jsonParserUtils = new JSONParserUtils(application);
            messageAPIDataSource = new MessageAPIMockDataSource(jsonParserUtils);
        } else {
            messageAPIDataSource = new MessageAPIDataSource(getDao(application), application.getString(R.string.API_KEY));
        }

        return new MessageRepository(messageRemoteDataSource,
                messageLocalDataSource,
                messageAPIDataSource);
    }

    public ConversationRepository getConversationRepository(Application application) {
        BaseConversationRemoteDataSource conversationRemoteDataSource;
        BaseConversationLocalDataSource conversationLocalDataSource;

        conversationRemoteDataSource = new ConversationFirebaseDataSource();
        conversationLocalDataSource = new ConversationLocalDataSource(getDao(application));

        return new ConversationRepository(conversationRemoteDataSource, conversationLocalDataSource);
    }

    public PetRepository getPetRepository(Application application) {
        BasePetRemoteDataSource petRemoteDataSource;
        BasePetLocalDataSource petLocalDataSource;

        petRemoteDataSource = new PetFirebaseDataSource();
        petLocalDataSource = new PetLocalDataSource(getDao(application));

        return new PetRepository(petRemoteDataSource, petLocalDataSource);
    }


    public ResponseAPIService getResponseAPIService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(ResponseAPIService.class);
    }
}
