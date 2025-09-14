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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
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
import com.example.beagle.model.Result;
import com.example.beagle.repository.message.MessageRepository;
import com.example.beagle.ui.chat.viewmodel.MessageViewModel;
import com.example.beagle.ui.chat.viewmodel.MessageViewModelFactory;
import com.example.beagle.util.Constants;
import com.example.beagle.util.JSONParserUtils;
import com.example.beagle.util.ServiceLocator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    boolean active = true;

    private List<Message> messageList;
    private MessageRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private MessageViewModel messageViewModel;


    // Dichiarazione vari attributi
    private AtomicInteger seq;
    private TextInputEditText editTextPrompt;
    private Button addPetButton;
    private ImageButton sendButton;
    private TextView welcomeTextView;

    // Per stringhe
    private Resources res;

    private FirebaseDatabase database = FirebaseDatabase.getInstance(Constants.FIREBASE_REALTIME_DATABASE);
    private DatabaseReference dbRef;


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


        // SetUp MessageRepository
        MessageRepository messageRepository =
                ServiceLocator.getInstance().getMessageRepository(
                        requireActivity().getApplication()
                );

        // SetUp ViewModel
        messageViewModel = new ViewModelProvider(
                requireActivity(),
                new MessageViewModelFactory(messageRepository)).get(MessageViewModel.class);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView");
        if (savedInstanceState != null)
            Log.d(TAG, savedInstanceState.toString());

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Setup Recycler Adapter con ArrayList messageList
        recyclerView = view.findViewById(R.id.recyclerViewChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        messageList = new ArrayList<Message>();

        adapter = new MessageRecyclerAdapter(R.layout.message, messageList);
        recyclerView.setAdapter(adapter);




        return view;
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG,"onViewCreated");
        Log.d(TAG, "conversationId: " + conversationId);

        // Dichiarazione vari attributi
        seq = new AtomicInteger(0);
        editTextPrompt = view.findViewById(R.id.textInputPromptChat);
        addPetButton = view.findViewById(R.id.addPetButton);
        sendButton = view.findViewById(R.id.imageSendButton);
        welcomeTextView = view.findViewById(R.id.textViewFirstWelcome);
        messageList.clear();

        // Per stringhe
        res = getResources();

        if (!hasPetSaved()) {
            welcomeTextView.setVisibility(View.VISIBLE);
            welcomeTextView.setText(String.format(res.getString(R.string.no_pet)));
            addPetButton.setVisibility(View.VISIBLE);
            setPromptEnabled(sendButton, editTextPrompt, false);

        } else {
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
                welcomeTextView.setVisibility(View.VISIBLE);
                welcomeTextView.setText(String.format(res.getString(R.string.saluto_iniziale),
                        pet.getName()));

                addPetButton.setVisibility(View.GONE);
                setPromptEnabled(sendButton, editTextPrompt, true);

            } else {
                Log.d(TAG, "Else else");
                // Carica messaggi relativi alla conversazione esistente
                //messageList.addAll(ServiceLocator.getInstance().getDao(requireActivity().getApplication()).messageDao().getMessages(conversationId));


                messageViewModel.getMessages(conversationId, false).observe(getViewLifecycleOwner(),
                        result -> {
                            if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                                if (result.isSuccess()) {
                                    Log.d(TAG, "LOCAL DATA CHANGE");
                                    //int initialSize = this.messageList.size()
                                    messageList.clear();
                                    messageList.addAll(((Result.MessageSuccess) result).getData().getMessages());
                                    adapter.notifyItemRangeInserted(0, messageList.size() - 1);

                                    //recyclerView.setVisibility(View.VISIBLE);
                                    seq.set(adapter.getItemCount());

                                    Log.d(TAG, "AFTER NOTIFY");
                                    Log.d(TAG, messageList.isEmpty() + "");
                                    //Log.d(TAG, "View: " + view);

                                    addPetButton.setVisibility(View.GONE);
                                    setPromptEnabled(sendButton, editTextPrompt, true);

                                } else {
                                    Snackbar.make(view, "ERROR_RETRIVING_MESSAGES", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
            Log.d(TAG, "BEFORE OR AFTER=");

        }





/*
        dbRef = FirebaseDatabase.getInstance().getReference("messages").child(Long.toString(conversationId));

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.d(TAG, "ONDATACHANGE FOR FIREBASE");
                messageViewModel.getMessages(conversationId, true).observe(getViewLifecycleOwner(),
                        result -> {
                            if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                                if (result.isSuccess()) {
                                    Log.d(TAG, "REMOTE DATA CHANGE");

                                    messageList.clear();
                                    messageList.addAll(((Result.MessageSuccess) result).getData().getMessages());
                                    adapter.notifyItemRangeInserted(0, messageList.size() - 1);

                                    recyclerView.setVisibility(View.VISIBLE);
                                    seq.set(adapter.getItemCount());
                                }
                            }
                        });
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dbRef.addValueEventListener(postListener);




*/





        addPetButton.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_chatFragment_to_profileFragment);
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
                // myRef.child(messageQuestion.getConversationId()+"").child(messageQuestion.getSeq()+"").setValue(messageQuestion);

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
                    NavHostFragment.findNavController(ChatFragment.this)
                            .navigate(R.id.action_chatFragment_to_conversationsHistoryFragment);
                    //Navigation.findNavController(view).navigate(R.id.action_chatFragment_to_conversationsHistoryFragment);
                    return true;
                }
                if (menuItem.getItemId() == R.id.action_pet_profile) {
                    Navigation.findNavController(view)
                            .navigate(R.id.action_chatFragment_to_profileFragment);
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
        Log.d(TAG, "SAVE MESSAGE" + message);
        //ServiceLocator.getInstance().getDao(requireActivity().getApplication()).messageDao().insert(message);
        messageViewModel.addMessage(message);


        //DatabaseReference myRef = database.getReference();
        //myRef.child("messages").child(message.getConversationId()+"").child(message.getSeq()+"").setValue(message.getMessageContent());

    }
    private void sendMessage(Message message) {
        // chiamata API
    }

    // Mostra il messaggio sulla schermata
    private void showMessage(Message message, List<Message> messageList,
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