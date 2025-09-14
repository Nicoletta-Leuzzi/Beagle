package com.example.beagle.ui.chat.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.beagle.R;
import com.example.beagle.adapter.MessageRecyclerAdapter;
import com.example.beagle.model.Conversation;
import com.example.beagle.model.Message;
import com.example.beagle.model.Pet;
import com.example.beagle.source.chat.ChatCompletionResponse;
import com.example.beagle.ui.profile.ProfileActivity;
import com.example.beagle.util.Constants;
import com.example.beagle.util.JSONParserUtils;
import com.example.beagle.util.ServiceLocator;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {


    // TODO: Da rimuovere dopo con ultimo animale se esiste (e mettere in luogo più adeguato)
    Pet pet = new Pet("420", "Among Us", (byte) 1, "Test", 123);
    long petId = Long.parseLong(pet.getIdToken());


    // TODO: Rimuovere i log DOPO aver finito, ora mi servono ancora
    public String TAG = "TEST";

    private long conversationId = 0;


    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");

        Log.d(TAG, "conversationId: " + conversationId + " dovrebbe essere 0");
        // Dal bundle, prende il conversationId (se esiste, altrimenti prende 0)
        // NOTA: key forzatamente da definire nel Constants
        assert getArguments() != null;
        conversationId = getArguments().getLong(Constants.CONVERSATION_BUNDLE_KEY);
        //petId = getArguments().getLong(Constants.PET_BUNDLE_KEY);
        // Preso info dal bundle
        Log.d(TAG, "conversationId: " + conversationId + " dovrebbe essere o 0, o quello della conversazione");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView");
        if (savedInstanceState != null)
            Log.d(TAG, savedInstanceState.toString());
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG,"onViewCreated");

        // Setup Recycler Adapter con ArrayList messageList
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        ArrayList<Message> messageList = new ArrayList<Message>();



        Log.d(TAG, "conversationId: " + conversationId);

        if (conversationId == 0) {
            Log.d(TAG, "App appena aperta, si crea nuova chat");

            // Creazione nuova conversazione
            Conversation activeConversation = new Conversation(petId);

            // Salvataggio conversazione su DB
            ServiceLocator.getInstance().getDao(requireActivity().getApplication()).conversationDao().insert(activeConversation);
            Log.d(TAG, "conversationId: " + conversationId + " dovrebbe essere 0");

            // Recupero id della conversazione, in quanto autogenerata solo dopo il salvataggio su DB
            conversationId = ServiceLocator.getInstance().getDao(requireActivity().getApplication()).conversationDao().getLastConversation(petId).getConversationId();
            Log.d(TAG, "conversationId: " + conversationId + " dovrebbe essere id autogenerato");
        }
        else {
            // Carica messaggi relativi alla conversazione esistente
            messageList.addAll(ServiceLocator.getInstance().getDao(requireActivity().getApplication()).messageDao().getMessages(conversationId));
        }

        MessageRecyclerAdapter adapter = new MessageRecyclerAdapter(R.layout.message, messageList);
        recyclerView.setAdapter(adapter);


        // Dichiarazione vari attributi
        AtomicInteger seq = new AtomicInteger(adapter.getItemCount());
        TextInputEditText editTextPrompt = view.findViewById(R.id.textInputPromptChat);
        Button addPetButton = view.findViewById(R.id.addPetButton);
        ImageButton sendButton = view.findViewById(R.id.imageSendButton);
        TextView welcomeTextView = view.findViewById(R.id.textViewFirstWelcome);

        // Per stringhe
        Resources res = getResources();


        // Se esiste un animale salvato, forma screen normale
        if (messageList.isEmpty()) {
            welcomeTextView.setVisibility(View.VISIBLE);

            if (hasPetSaved()) {
                welcomeTextView.setText(String.format(res.getString(R.string.saluto_iniziale),
                        pet.getName()));
                addPetButton.setVisibility(View.GONE);
                setPromptEnabled(sendButton, editTextPrompt, true);
            }

            // Altrimenti, forma screen con bottone aggiungi animale
            else {
                welcomeTextView.setText(String.format(res.getString(R.string.no_pet)));
                addPetButton.setVisibility(View.VISIBLE);
                setPromptEnabled(sendButton, editTextPrompt, false);
            }
        }


        addPetButton.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), ProfileActivity.class));
        });


        Log.d(TAG, requireActivity().toString());
        Log.d(TAG, requireActivity().getApplication().toString());

        // TODO: finire codice
        long finalConversationId = conversationId;
        sendButton.setOnClickListener(v -> {
            // Se il prompt non è vuoto
            if (!Objects.requireNonNull(editTextPrompt.getText()).toString().trim().isEmpty()) {


                String question = editTextPrompt.getText().toString();

                // Disabilita scrittura ed invio
                editTextPrompt.setText("");
                welcomeTextView.setVisibility(View.GONE);
                setPromptEnabled(sendButton, editTextPrompt, false);

                Message messageQuestion = createMessage(finalConversationId, seq.getAndIncrement(),
                        true, question);

                saveMessage(messageQuestion, getActivity());
                // TODO: send to AI
                sendMessage(messageQuestion); // con un listener nell'adapter?
                // TODO: chiarire come deve mostrare il messaggio (da DB è più corretto?)
                showMessage(messageQuestion, messageList, adapter);



                // RISPOSTA?
                // TODO: AI reply
                String answer = getMessageAPI_WIP();
                Message messageAnswer = createMessage(finalConversationId, seq.getAndIncrement(),
                        false, answer);
                saveMessage(messageAnswer, getActivity());
                showMessage(messageAnswer, messageList, adapter);

                setPromptEnabled(sendButton, editTextPrompt, true);

            // Se il prompt è vuoto
            } else {
                editTextPrompt.setError(String.format(res.getString(R.string.no_text)));
            }
        });



        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear(); // pulisce menu prima di aggiungere elementi di questo fragment
                menuInflater.inflate(R.menu.menu_chat, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_history) {
                    //NavHostFragment.findNavController(ChatFragment.this)
                    //        .navigate(R.id.action_chatFragment_to_conversationsHistoryFragment);
                    Navigation.findNavController(view).navigate(R.id.action_chatFragment_to_conversationsHistoryFragment);
                    return true;
                }
                if (menuItem.getItemId() == R.id.action_pet_profile) {
                    startActivity(new Intent(requireContext(), ProfileActivity.class));
                }
                return false;
            }

        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }




    // TODO: da spostare tutti i metodi in classe(i) più adeguata(e)



    // Mostra il messaggio in Chat
    private Message createMessage(long conversationId, int seq,
                               boolean fromUser, String messageContent) {
        return new Message(conversationId, seq, fromUser, messageContent);
    }


    private void saveMessage(Message message, Context context) {
        ServiceLocator.getInstance().getDao(requireActivity().getApplication()).messageDao().insert(message);
    }
    private void sendMessage(Message message) {
        // chiamata API
    }

    // Mostra il messaggio sulla schermata
    private void showMessage(Message message, ArrayList<Message> messageList,
                             MessageRecyclerAdapter adapter) {
        messageList.add(message);
        adapter.notifyItemInserted(messageList.size() -1);
    }

    private static final String TAG_API = ChatFragment.class.getName();
    // TODO: dovrebbe ritornare la reply dell'AI
    private String getMessageAPI_WIP() {
        String REPLY = "";
        /*
        JSONParserUtils JSONParser = new JSONParserUtils(getContext());
        // CODICE DA RIPENSARE E ADATTARE
        try {
            ChatCompletionResponse response = JSONParser.parseJSONResponseWithGson(Constants.SAMPLE_JSON_FILENAME);

            Log.i(TAG, response.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        } */
        return REPLY;
    }


    // TODO: dovrebbe ritornarse true se esiste almeno un animale salvato
    private boolean hasPetSaved() {
        return true;
    }


    // Abilita/Disabilita scrittura in Chat e tasto invio
    private void setPromptEnabled(ImageButton sendButton,
                                  TextInputEditText editTextPrompt, boolean b) {
        sendButton.setEnabled(b);
        sendButton.setSelected(b);
        editTextPrompt.setEnabled(b);
    }

    // Non penso sia più necessario, ma lo tengo per ora
    private void populateChat(MessageRecyclerAdapter adapter, ArrayList<Message> messageList) {
        for(int i = 0; i < messageList.size() - 1; i++) {
            adapter.notifyDataSetChanged();
        }
    }

}