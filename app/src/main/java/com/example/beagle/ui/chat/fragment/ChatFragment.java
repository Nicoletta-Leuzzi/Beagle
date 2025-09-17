package com.example.beagle.ui.chat.fragment;

import static com.example.beagle.util.Constants.PET_ID_BUNDLE_KEY;
import static com.example.beagle.util.Constants.PET_NAME_BUNDLE_KEY;
import static com.example.beagle.util.Constants.SHARED_PREFERENCES_ACTIVE_PET_ID;
import static com.example.beagle.util.Constants.SHARED_PREFERENCES_ACTIVE_PET_NAME;
import static com.example.beagle.util.Constants.SHARED_PREFERENCES_FILENAME;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
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
import com.example.beagle.repository.conversation.ConversationRepository;
import com.example.beagle.repository.message.MessageRepository;
import com.example.beagle.ui.chat.viewmodel.conversation.ConversationViewModel;
import com.example.beagle.ui.chat.viewmodel.conversation.ConversationViewModelFactory;
import com.example.beagle.ui.chat.viewmodel.message.AIReplyViewModel;
import com.example.beagle.ui.chat.viewmodel.message.AIReplyViewModelFactory;
import com.example.beagle.ui.chat.viewmodel.message.MessageViewModel;
import com.example.beagle.ui.chat.viewmodel.message.MessageViewModelFactory;
import com.example.beagle.util.Constants;
import com.example.beagle.util.ServiceLocator;
import com.example.beagle.util.SharedPreferencesUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    //Pet pet = new Pet("Among Us", (byte) 1, "Test", 123);
    //long petId = Long.parseLong(pet.getIdToken());



    // TODO: Rimuovere i log DOPO aver finito, ora mi servono ancora
    public String TAG = "TEST";

    private long conversationId;
    private long petId;
    private String petName;
    private Conversation activeConversation;
    private List<Message> messageList;
    private MessageRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private MessageViewModel messageViewModel;
    private AIReplyViewModel aiReplyViewModel;
    private ConversationViewModel conversationViewModel;


    // Dichiarazione vari attributi
    private AtomicInteger seq = new AtomicInteger(0);
    private int messageSeq;
    private TextInputEditText editTextPrompt;
    private Button addPetButton;
    private ImageButton sendButton;
    private TextView welcomeTextView;

    // Per stringhe
    private Resources res;

    private FirebaseDatabase database = FirebaseDatabase.getInstance(Constants.FIREBASE_REALTIME_DATABASE);
    private DatabaseReference dbRef;

    private MutableLiveData<Result> getMessagesMutableLiveData;
    private MutableLiveData<Result> saveUserMessageMutableLiveData;
    private MutableLiveData<Result> saveAIMessageMutableLiveData;
    private MutableLiveData<Result> createConversationMutableLiveData;
    private SharedPreferencesUtils sharedPreferencesUtils;



    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("asd", "");
        Log.d("asd", "");
        Log.d("asd", "");
        Log.d(TAG,"onCreate");

        sharedPreferencesUtils = new SharedPreferencesUtils(requireActivity().getApplication());

        Log.d(TAG, "conversationId: " + conversationId + " dovrebbe essere 0");
        // Dal bundle, prende il conversationId (se esiste, altrimenti prende 0)
        // NOTA: key forzatamente da definire nel Constants
        assert getArguments() != null;
        conversationId = getArguments().getLong(Constants.CONVERSATION_BUNDLE_KEY);
        Log.d(TAG, "ID FROM BUNDLE: " + conversationId);
        Log.d("asd", "ID FROM BUNDLE: " + conversationId);
        //petId = getArguments().getLong(PET_ID_BUNDLE_KEY);
        //petName = getArguments().getString(PET_NAME_BUNDLE_KEY);

        // petId = getArguments().getLong(Constants.PET_BUNDLE_KEY);
        // Preso info dal bundle


        if (sharedPreferencesUtils.readStringData(
                SHARED_PREFERENCES_FILENAME, SHARED_PREFERENCES_ACTIVE_PET_ID) != null &&
                sharedPreferencesUtils.readStringData(
                        SHARED_PREFERENCES_FILENAME, SHARED_PREFERENCES_ACTIVE_PET_NAME) != null) {
            String petIdString = sharedPreferencesUtils.readStringData(SHARED_PREFERENCES_FILENAME,
                    SHARED_PREFERENCES_ACTIVE_PET_ID);
            petId = Long.parseLong(petIdString);

            petName = sharedPreferencesUtils.readStringData(SHARED_PREFERENCES_FILENAME,
                    SHARED_PREFERENCES_ACTIVE_PET_NAME);
        } else {
            petId = 0;
        }







        // SetUp MessageRepository
        MessageRepository messageRepository =
                ServiceLocator.getInstance().getMessageRepository(
                        requireActivity().getApplication(),
                        requireActivity()
                                .getApplication().getResources().getBoolean(R.bool.debug_mode)
                );

        ConversationRepository conversationRepository =
                ServiceLocator.getInstance().getConversationRepository(
                        requireActivity().getApplication()
                );

        // SetUp ViewModel
        messageViewModel = new ViewModelProvider(
                requireActivity(),
                new MessageViewModelFactory(messageRepository))
                .get(MessageViewModel.class);

        aiReplyViewModel = new ViewModelProvider(
                requireActivity(),
                new AIReplyViewModelFactory(messageRepository))
                .get(AIReplyViewModel.class);

        conversationViewModel = new ViewModelProvider(
                requireActivity(),
                new ConversationViewModelFactory(conversationRepository))
                .get(ConversationViewModel.class);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Setup Recycler Adapter con ArrayList messageList
        recyclerView = view.findViewById(R.id.recyclerViewChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        messageList = new ArrayList<Message>();
        Log.d(TAG, "IS MESSAGE LIST EMPTY? " + messageList.isEmpty());

        adapter = new MessageRecyclerAdapter(R.layout.message, messageList);
        recyclerView.setAdapter(adapter);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
        adapter.notifyDataSetChanged();



        getMessagesMutableLiveData = messageViewModel.getMessages(conversationId, false);
        Log.d(TAG,"");
        Log.d(TAG, "THIS IS AFTER assegnamento getMessagesMutableLiveData" + Long.toString(conversationId));
        Log.d(TAG,"");
        if (!getMessagesMutableLiveData.hasActiveObservers()) {
            getMessagesMutableLiveData.observe(getViewLifecycleOwner(),
                    result -> {
                        Log.d(TAG, "getMessages");
                        Log.d(TAG, "THIS IS INSIDE assegnamento getMessagesMutableLiveData" + Long.toString(conversationId));
                        if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                            if (result.isSuccess()) {
                                Log.d(TAG, "getMessagesObserver");
                                //int initialSize = this.messageList.size()
                                messageList.clear();
                                messageList.addAll(((Result.MessageReadSuccess) result).getData());
                                adapter.notifyItemRangeChanged(seq.get(), messageList.size() - 1);

                                recyclerView.setVisibility(View.VISIBLE);
                                seq.set(adapter.getItemCount());


                                //addPetButton.setVisibility(View.GONE);
                                setPromptEnabled(sendButton, editTextPrompt, true);

                            } else {
                                Log.d(TAG, "getMessages ELSE error");
                                Snackbar.make(view, "ERROR_RETRIVING_MESSAGES", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        return view;
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG,"onViewCreated");

        // Dichiarazione vari attributi
        editTextPrompt = view.findViewById(R.id.textInputPromptChat);
        addPetButton = view.findViewById(R.id.addPetButton);
        sendButton = view.findViewById(R.id.imageSendButton);
        welcomeTextView = view.findViewById(R.id.textViewFirstWelcome);
        messageList.clear();

        // Per stringhe
        res = getResources();

        if (petId == 0) {
            welcomeTextView.setVisibility(View.VISIBLE);
            welcomeTextView.setText(String.format(res.getString(R.string.no_pet)));
            addPetButton.setVisibility(View.VISIBLE);
            setPromptEnabled(sendButton, editTextPrompt, false);

        } else {
            if (conversationId == 0) {
                welcomeTextView.setVisibility(View.VISIBLE);
                welcomeTextView.setText(String.format(res.getString(R.string.initial_greeting), petName));
                Log.d(TAG, "App appena aperta, si crea nuova chat");

                // Creazione nuova conversazione
                //activeConversation = new Conversation(petId);



                welcomeTextView.setText(String.format(res.getString(R.string.initial_greeting), petName));
                welcomeTextView.setVisibility(View.VISIBLE);

                addPetButton.setVisibility(View.GONE);
                setPromptEnabled(sendButton, editTextPrompt, true);

            } else {
                Log.d(TAG, "Else conversazione aperta");
                messageViewModel.getMessages(conversationId, false);
            }
        }



        addPetButton.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_chatFragment_to_profileFragment);
        });


        sendButton.setOnClickListener(v -> {
            // Se il prompt non è vuoto
            if (!Objects.requireNonNull(editTextPrompt.getText()).toString().trim().isEmpty()) {
                activeConversation = new Conversation(petId);
                String question = editTextPrompt.getText().toString();

                // Disabilita scrittura ed invio
                editTextPrompt.setText("");
                welcomeTextView.setVisibility(View.GONE);

                setPromptEnabled(sendButton, editTextPrompt, false);
                messageSeq = seq.getAndIncrement();


                if (conversationId == 0) {
                    Log.d("asd", "conversationId: " + conversationId + " dovrebbe essere 0");
                    Log.d(TAG, "conversationId: " + conversationId + " dovrebbe essere 0");
                    //Log.d("asd", "createConversationMutableLiveData.hasActiveObservers()" + createConversationMutableLiveData.hasActiveObservers());
                    createConversationMutableLiveData = conversationViewModel.addConversation(activeConversation, petId);
                    Log.d("asd", "conversationId: " + conversationId + "DOPO ASSEGNAMENTO CONVERSATION MUTABLE");
                    createConversationMutableLiveData.observe(getViewLifecycleOwner(),
                            result -> {
                                Log.d("asd", "conversationId: " + conversationId + "CONVERSATION OBSERVER - START");

                                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                                    if (result.isSuccess()) {
                                        Log.d(TAG, "SUCCESS CONVERSATION");

                                        conversationId = (((Result.ConversationSuccess) result).getData()).get(0).getConversationId();
                                        Log.d(TAG, "conversationId: " + conversationId + " dovrebbe essere id autogenerato");
                                        Log.d("ASD", "conversationId: " + conversationId + " dovrebbe essere id autogenerato");

                                        Message messageQuestion = createMessage(conversationId, messageSeq,
                                                true, question);
                                        Log.d("asd", Long.toString(conversationId) + "Before saveMessage");
                                        saveMessage(messageQuestion, conversationId, messageSeq, view);
                                        createConversationMutableLiveData.removeObservers(getViewLifecycleOwner());
                                    } else {
                                        // result NOT success
                                    }
                                } // not resume
                            });
                } else {
                    Log.d("asd", "NON dovrebbe creare nuova conversazione" + conversationId);
                    Message messageQuestion = createMessage(conversationId, messageSeq,
                            true, question);
                    saveMessage(messageQuestion, conversationId, messageSeq, view);
                }
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

                    Bundle bundle = new Bundle();
                    bundle.putLong(PET_ID_BUNDLE_KEY, petId);

                    NavHostFragment.findNavController(ChatFragment.this)
                            .navigate(R.id.action_chatFragment_to_conversationsHistoryFragment, bundle);
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

    private Message createMessage(long conversationId, int seq,
                               boolean fromUser, String messageContent) {
        return new Message(conversationId, seq, fromUser, messageContent);
    }


    private void saveMessage(Message message, long conversationId, int messageSeq, View view) {
        Log.d(TAG, "SAVE MESSAGE" + message);
        Log.d(TAG, "AAAAAAAAAAAAAAAAAAA"+Long.toString(conversationId));

        Log.d("asd", Long.toString(conversationId) + "Before all observers");


        saveUserMessageMutableLiveData = messageViewModel.addMessage(message, conversationId, messageSeq);
        if (!saveUserMessageMutableLiveData.hasActiveObservers()) {
            saveUserMessageMutableLiveData.observe(getViewLifecycleOwner(),
                    result -> {
                        Log.d("asd", Long.toString(conversationId) + "saveMessage - Inside observer");
                        Log.d(TAG, "AAAAAAAAAAAAAAAAAAA"+Long.toString(conversationId));
                        Log.d(TAG, "saveMessage - Inside observer");
                        if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                            Log.d(TAG, "saveMessage - RESUMED");
                            Log.d("asd", Long.toString(conversationId) + "saveMessage - Inside observer");
                            if (result.isSuccess()) {
                                Log.d(TAG, "saveMessage - RESUMED - SUCCESS");

                                Log.d(TAG, "AAAAAAAAAAAAAAAAAAA"+Long.toString(conversationId));
                                saveAIMessageMutableLiveData = aiReplyViewModel.getAIReply(conversationId, seq.get());
                                if (!saveAIMessageMutableLiveData.hasActiveObservers()) {
                                    Log.d(TAG, "getAIReply has no active observer");
                                    saveAIMessageMutableLiveData.observe(getViewLifecycleOwner(),
                                            replyResult -> {
                                                Log.d(TAG, "AAAAAAAAAAAAAAAAAAA"+Long.toString(conversationId));
                                                Log.d("asd", Long.toString(conversationId) + "getAIReply - Inside observer");
                                                Log.d(TAG, "getAIReply - Inside observer");
                                                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                                                    Log.d(TAG, "getAIReply - RESUMED");
                                                    if (replyResult.isSuccess()) {
                                                        Log.d(TAG, "getAIReply - RESUMED - SUCCESS");
                                                        Log.d("asd", Long.toString(conversationId) + "getAIReply - RESUMED - SUCCESS");

                                                    } else {
                                                        Log.d(TAG, "getAIReply - RESUMED - FAILURE");
                                                        Snackbar.make(view, "ERROR_GETTING_AI_REPLY", Snackbar.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Log.d(TAG, "getAIReply - NOT RESUMED");
                                                }
                                                saveAIMessageMutableLiveData.removeObservers(getViewLifecycleOwner());
                                                Log.d(TAG, "getAIReply - END");
                                            });
                                } else {
                                    Log.d(TAG, "getAIReply has active observer");
                                }


                            } else {
                                Log.d(TAG, "saveMessage - RESUMED - FAILURE");
                                Snackbar.make(view, "ERROR_ADDING_MESSAGE", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "saveMessage - NOT RESUMED");
                        }
                        saveUserMessageMutableLiveData.removeObservers(getViewLifecycleOwner());
                        Log.d(TAG, "saveMessage - END");

                    });
        } else {
            Log.d(TAG, "saveMessage has active observer");
        }


        /*
        if (!messageViewModel.addMessage(message, conversationId, messageSeq).hasActiveObservers()) {
            Log.d(TAG, "saveMessage has no active observers");
            messageViewModel.addMessage(message, conversationId, messageSeq).observe(getViewLifecycleOwner(),
                    result -> {
                        Log.d("asd", Long.toString(conversationId) + "saveMessage - Inside observer");
                        Log.d(TAG, "AAAAAAAAAAAAAAAAAAA"+Long.toString(conversationId));
                        Log.d(TAG, "saveMessage - Inside observer");
                        if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                            Log.d(TAG, "saveMessage - RESUMED");
                            Log.d("asd", Long.toString(conversationId) + "saveMessage - Inside observer");
                            if (result.isSuccess()) {
                                Log.d(TAG, "saveMessage - RESUMED - SUCCESS");

                                Log.d(TAG, "AAAAAAAAAAAAAAAAAAA"+Long.toString(conversationId));
                                if (!aiReplyViewModel.getAIReply(conversationId, seq.get()).hasActiveObservers()) {
                                    Log.d(TAG, "getAIReply has no active observer");
                                    aiReplyViewModel.getAIReply(conversationId, seq.get()).observe(getViewLifecycleOwner(),
                                            replyResult -> {
                                                Log.d(TAG, "AAAAAAAAAAAAAAAAAAA"+Long.toString(conversationId));
                                                Log.d("asd", Long.toString(conversationId) + "getAIReply - Inside observer");
                                                Log.d(TAG, "getAIReply - Inside observer");
                                                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                                                    Log.d(TAG, "getAIReply - RESUMED");
                                                    if (replyResult.isSuccess()) {
                                                        Log.d(TAG, "getAIReply - RESUMED - SUCCESS");
                                                        Log.d("asd", Long.toString(conversationId) + "getAIReply - RESUMED - SUCCESS");

                                                    } else {
                                                        Log.d(TAG, "getAIReply - RESUMED - FAILURE");
                                                        Snackbar.make(view, "ERROR_GETTING_AI_REPLY", Snackbar.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Log.d(TAG, "getAIReply - NOT RESUMED");
                                                }
                                                Log.d(TAG, "getAIReply - END");
                                            });
                                } else {
                                    Log.d(TAG, "getAIReply has active observer");
                                }


                            } else {
                                Log.d(TAG, "saveMessage - RESUMED - FAILURE");
                                Snackbar.make(view, "ERROR_ADDING_MESSAGE", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "saveMessage - NOT RESUMED");
                        }
                        Log.d(TAG, "saveMessage - END");

                    });
        } else {
            Log.d(TAG, "saveMessage has active observer");
        }

         */


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

}