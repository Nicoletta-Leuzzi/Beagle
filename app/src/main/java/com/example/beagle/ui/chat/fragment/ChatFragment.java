package com.example.beagle.ui.chat.fragment;

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
import com.example.beagle.ui.chat.viewmodel.message.MessageViewModel;
import com.example.beagle.ui.chat.viewmodel.message.MessageViewModelFactory;
import com.example.beagle.util.Constants;
import com.example.beagle.util.ServiceLocator;
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
    Pet pet = new Pet("420", "Among Us", (byte) 1, "Test", 123);
    long petId = Long.parseLong(pet.getIdToken());


    // TODO: Rimuovere i log DOPO aver finito, ora mi servono ancora
    public String TAG = "TEST";

    private long conversationId;
    private Conversation activeConversation;

    boolean active = true;

    private List<Message> messageList;
    private MessageRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private MessageViewModel messageViewModel;


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
    ValueEventListener postListener;
    Message lastMessage;


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
                        requireActivity().getApplication(),
                        requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
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

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Setup Recycler Adapter con ArrayList messageList
        recyclerView = view.findViewById(R.id.recyclerViewChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        messageList = new ArrayList<Message>();

        adapter = new MessageRecyclerAdapter(R.layout.message, messageList);
        recyclerView.setAdapter(adapter);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);



        messageViewModel.getMessages(conversationId, false)
                .observe(getViewLifecycleOwner(),
                        result -> {
                            Log.d(TAG, "getMessages");
                            if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                                if (result.isSuccess()) {
                                    Log.d(TAG, "getMessagesObserver");
                                    //int initialSize = this.messageList.size()
                                    messageList.clear();
                                    messageList.addAll(((Result.MessageReadSuccess) result).getData());
                                    //adapter.notifyItemRangeInserted(0, messageList.size() - 1);
                                    adapter.notifyItemRangeChanged(seq.get(), messageList.size()-1);

                                    //adapter.notifyDataSetChanged();
                                    /*
                                    List<Message> temp = ((Result.MessageReadSuccess) result).getData();
                                    for (int i = 0; i < temp.size(); i++) {
                                        messageList.add(temp.get(i));
                                        adapter.notifyItemInserted(i);
                                    }
                                     */

                                    recyclerView.setVisibility(View.VISIBLE);
                                    seq.set(adapter.getItemCount());


                                    addPetButton.setVisibility(View.GONE);
                                    setPromptEnabled(sendButton, editTextPrompt, true);
                                } else {
                                    Log.d(TAG, "getMessages ELSE error");
                                    Snackbar.make(view, "ERROR_RETRIVING_MESSAGES", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });




        return view;
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG,"onViewCreated");

        // Dichiarazione vari attributi
        //seq = new AtomicInteger(0);
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
                welcomeTextView.setVisibility(View.VISIBLE);
                welcomeTextView.setText(String.format(res.getString(R.string.initial_greeting), pet.getName()));
                Log.d(TAG, "App appena aperta, si crea nuova chat");

                // Creazione nuova conversazione
                activeConversation = new Conversation(petId);

                // Salvataggio conversazione su DB
                ServiceLocator.getInstance().getDao(requireActivity().getApplication()).conversationDao().insert(activeConversation);
                Log.d(TAG, "conversationId: " + conversationId + " dovrebbe essere 0");

                // Recupero id della conversazione, in quanto autogenerata solo dopo il salvataggio su DB
                conversationId = ServiceLocator.getInstance().getDao(requireActivity().getApplication()).conversationDao().getLastConversation(petId).getConversationId();
                Log.d(TAG, "conversationId: " + conversationId + " dovrebbe essere id autogenerato");

                welcomeTextView.setText(String.format(res.getString(R.string.initial_greeting),
                        pet.getName()));
                welcomeTextView.setVisibility(View.VISIBLE);

                addPetButton.setVisibility(View.GONE);
                setPromptEnabled(sendButton, editTextPrompt, true);

            } else {
                Log.d(TAG, "Else conversazione aperta");
                messageViewModel.getMessages(conversationId, false);
                /*
                // Carica messaggi relativi alla conversazione esistente
                //messageList.addAll(ServiceLocator.getInstance().getDao(requireActivity().getApplication()).messageDao().getMessages(conversationId));
                if (!messageViewModel.getMessages(conversationId, false).hasActiveObservers()) {
                    messageViewModel.getMessages(conversationId, false)
                            .observe(getViewLifecycleOwner(),
                                    result -> {
                                        if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                                            if (result.isSuccess()) {
                                                //int initialSize = this.messageList.size()
                                                messageList.clear();
                                                messageList.addAll(((Result.MessageSuccess) result).getData().getMessages());
                                                //adapter.notifyItemRangeInserted(0, messageList.size() - 1);
                                                adapter.notifyDataSetChanged();

                                                recyclerView.setVisibility(View.VISIBLE);
                                                seq.set(adapter.getItemCount());


                                                addPetButton.setVisibility(View.GONE);
                                                setPromptEnabled(sendButton, editTextPrompt, true);
                                            }
                                        } else {
                                            Snackbar.make(view, "ERROR_RETRIVING_MESSAGES", Snackbar.LENGTH_SHORT).show();
                                        }
                                    });
                } else {
                    messageViewModel.getMessages(conversationId, false);
                }


*/

            }

        }

        /*
        dbRef = database.getReference(FIREBASE_USERS_COLLECTION)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(FIREBASE_MESSAGES_COLLECTION)
                .child(Long.toString(conversationId));

         */
         // FIREROOM LISTENER
        /*
        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {


                    if (!messageViewModel.getMessages(conversationId, true).hasActiveObservers()) {
                        messageViewModel.getMessages(conversationId, true)
                                .observe(getViewLifecycleOwner(),
                                        result -> {
                                            if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                                                if (result.isSuccess()) {
                                                    messageList.clear();
                                                    messageList.addAll(((Result.MessageSuccess) result).getData().getMessages());

                                                    adapter.notifyDataSetChanged();

                                                    recyclerView.setVisibility(View.VISIBLE);
                                                    seq.set(adapter.getItemCount());
                                                }
                                            }
                                        });
                    } else {
                        messageViewModel.getMessages(conversationId, true);
                    }



                }
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
                messageSeq = seq.getAndIncrement();

                Message messageQuestion = createMessage(finalConversationId, messageSeq,
                        true, question);
                lastMessage = messageQuestion;
                int i = 0;

                saveMessage(messageQuestion, conversationId, messageSeq, view);

                // TODO: send to AI
                sendMessage(messageQuestion); // con un listener nell'adapter?
                // TODO: chiarire come deve mostrare il messaggio (da DB è più corretto?)
                //showMessage(messageQuestion, messageList, adapter);


                /*
                // RISPOSTA?
                // TODO: AI reply
                String answer = getMessageAPI_WIP();
                Message messageAnswer = createMessage(finalConversationId, seq.getAndIncrement(),
                        false, answer);
                // TODO: Commentato perchè troppo veloce e rovina i ViewModel
                //saveMessage(messageAnswer, conversationId, seq, view);
                //showMessage(messageAnswer, messageList, adapter);

                 */

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

    @Override
    public void onPause() {
        super.onPause();
        /*

        if (messageViewModel.getMessages(conversationId, false).hasActiveObservers()) {
            messageViewModel.getMessages(conversationId, false).removeObservers(getViewLifecycleOwner());
            Log.d(TAG, "RemovedGetMessagesObserver");
        }

         */
        /*

        if (messageViewModel.addMessage(lastMessage, conversationId, seq.get()).hasActiveObservers()) {
            messageViewModel.addMessage(lastMessage, conversationId, seq.get()).removeObservers(getViewLifecycleOwner());
            Log.d(TAG, "RemovedAddMessagesObserver");
        }

         */


    }




    // TODO: da spostare tutti i metodi in classe(i) più adeguata(e)



    // Mostra il messaggio in Chat
    private Message createMessage(long conversationId, int seq,
                               boolean fromUser, String messageContent) {
        return new Message(conversationId, seq, fromUser, messageContent);
    }


    private void saveMessage(Message message, long conversationId, int messageSeq, View view) {
        Log.d(TAG, "SAVE MESSAGE" + message);
        //ServiceLocator.getInstance().getDao(requireActivity().getApplication()).messageDao().insert(message);
        //messageViewModel.addMessage(message, conversationId, seq.get());
        //ServiceLocator.getInstance().getDao(requireActivity().getApplication()).messageDao().insert(message);
        //List<Message> messageListTemp = ServiceLocator.getInstance().getDao(requireActivity().getApplication()).messageDao().getMessages(conversationId);
        //messageList.clear();
        //messageList.addAll(messageListTemp);
        //adapter.notifyDataSetChanged();

        if (!messageViewModel.addMessage(message, conversationId, messageSeq).hasActiveObservers()) {
            Log.d(TAG, "saveMessage has no active observers");
            messageViewModel.addMessage(message, conversationId, messageSeq).observe(getViewLifecycleOwner(),
                    result -> {
                        Log.d(TAG, "saveMessage - Inside observer");
                        if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                            Log.d(TAG, "saveMessage - RESUMED");
                            if (result.isSuccess()) {
                                Log.d(TAG, "saveMessage - RESUMED - SUCCESS");
                                /*
                                messageList.clear();
                                messageList.addAll(((Result.MessageReadSuccess) result).getData());
                                adapter.notifyDataSetChanged();
                                seq.set(adapter.getItemCount());

                                 */


                            } else {
                                Log.d(TAG, "saveMessage - RESUMED - FAILURE");
                                Snackbar.make(view, "ERROR_ADDING_MESSAGE", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "saveMessage - NOT RESUMED");
                        }
                        Log.d(TAG, "saveMessage - END");
                        //setPromptEnabled(sendButton, editTextPrompt, true);
                    });
        } else {
            Log.d(TAG, "saveMessage has active observer");
        }

        //AtomicBoolean ssd = new AtomicBoolean(false);
        if (!messageViewModel.getAIReply(conversationId, seq.get()).hasActiveObservers()) {
            Log.d(TAG, "getAIReply has no active observer");
            messageViewModel.getAIReply(conversationId, seq.get()).observe(getViewLifecycleOwner(),
                    replyResult -> {
                        Log.d(TAG, "getAIReply - Inside observer");
                        if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                            Log.d(TAG, "getAIReply - RESUMED");
                            if (replyResult.isSuccess()) {
                                Log.d(TAG, "getAIReply - RESUMED - SUCCESS");
                                //messageViewModel.addMessage(((Result.MessageReadSuccess) replyResult).getData().get(0), conversationId, seq.get());
                                //messageList.clear();
                                //messageList.addAll(((Result.MessageReadSuccess) replyResult).getData());
                                //adapter.notifyDataSetChanged();
                                //seq.set(adapter.getItemCount());

                                //messageViewModel.getMessages(conversationId, false);
                            } else {
                                Log.d(TAG, "getAIReply - RESUMED - FAILURE");
                                Snackbar.make(view, "ERROR_GETTING_AI_REPLY", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "getAIReply - NOT RESUMED");
                        }
                        Log.d(TAG, "getAIReply - END");
                        //ssd.set(true);
                    });
        } else {
            Log.d(TAG, "getAIReply has active observer");
        }
    }




    private void sendMessage(Message message) {
        // chiamata API
    }

    // Mostra il messaggio sulla schermata
    private void showMessage(Message message, List<Message> messageList,
                             MessageRecyclerAdapter adapter) {
        //messageList.add(message);
        //adapter.notifyItemInserted(messageList.size() -1);
    }

    private static final String TAG_API = ChatFragment.class.getName();
    // TODO: dovrebbe ritornare la reply dell'AI
    private String getMessageAPI_WIP() {

        String REPLY = "REPLY";
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

}