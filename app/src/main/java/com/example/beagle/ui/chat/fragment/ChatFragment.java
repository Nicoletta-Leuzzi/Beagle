package com.example.beagle.ui.chat.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
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
import com.example.beagle.database.DataRoomDatabase;
import com.example.beagle.model.Conversation;
import com.example.beagle.model.Message;
import com.example.beagle.model.Pet;
import com.example.beagle.util.Constants;
import com.example.beagle.util.ServiceLocator;
import com.google.android.material.textfield.TextInputEditText;

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

    // private String messageContent;
    // TODO: Da rimuovere dopo con ultimo animale se esiste (e mettere in luogo più adeguato)
    Pet pet = new Pet("Among Us");
    // Conversation conversation;
    // get active conversation

    public String TAG = "TEST";
    private long conversationId = 6;


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
        Log.d(TAG, "AAAAA" + conversationId);
        assert getArguments() != null;
        conversationId = getArguments().getLong(Constants.BUNDLE_KEY);
        Log.d(TAG, "AAAAA" + conversationId);

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




        // RECYCLER ADAPTER STUFF
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        ArrayList<Message> messageList = new ArrayList<Message>();

        MessageRecyclerAdapter adapter = new MessageRecyclerAdapter(R.layout.message, messageList);
        recyclerView.setAdapter(adapter);





        // SUPPORTO PER LEGGERE CONVERSAZIONI
        // TODO: da sostituire con lettura da database (ConversationHistoryFragment passa un bundle con conversationId, e chiamata DAO filtrando con quello
        /*
        ArrayList<Message> testList = new ArrayList<>();
        long conversationId = 69;
        for(int i = 0; i < 5; i++) {
            testList.add(createMessage(conversationId, i, true, "blaf "+i));
        }
        messageList.addAll(testList);
        populateChat(adapter, messageList);
        */


        //long conversationId = 4;
        Log.d(TAG, "CCC"+conversationId);
        long petId = 420;
        ArrayList<Message> activeConvMessages = new ArrayList<>();
        if ((ServiceLocator.getInstance().getDao(requireActivity().getApplication()).conversationDao().getSingleConversation(petId, conversationId)) == null) {
            Conversation activeConversation = new Conversation(conversationId, petId);
            ServiceLocator.getInstance().getDao(requireActivity().getApplication()).conversationDao().insert(activeConversation);
        }
        else {
            activeConvMessages.addAll(ServiceLocator.getInstance().getDao(requireActivity().getApplication()).messageDao().getMessages(conversationId));
        }

        messageList.addAll(activeConvMessages);
        populateChat(adapter, messageList);



        // SET UP
        AtomicInteger seq = new AtomicInteger();
        TextInputEditText editTextPrompt = view.findViewById(R.id.textInputPrompt);
        Button addPetButton = view.findViewById(R.id.addPetButton);
        ImageButton sendButton = view.findViewById(R.id.imageSendButton);
        TextView welcomeTextView = view.findViewById(R.id.textView);

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
            // TODO: intent verso Pet activity
        });




        Log.d(TAG, requireActivity().toString());
        Log.d(TAG, requireActivity().getApplication().toString());

        // TODO: finire codice
        sendButton.setOnClickListener(v -> {
            // Se il prompt non è vuoto
            if (!Objects.requireNonNull(editTextPrompt.getText()).toString().trim().isEmpty()) {

                // CREA MESSAGGIO E LO MOSTRA
                String question = editTextPrompt.getText().toString();
                //showMessage(messageContent, messageList, adapter, true);
                Message messageQuestion = createMessage(conversationId, seq.getAndIncrement(),
                        true, question);
                showMessage(messageQuestion, messageList, adapter);
                // TODO: send to AI
                sendMessage(messageQuestion); // con un listener nell'adapter?
                saveMessage(messageQuestion, getActivity());
                editTextPrompt.setText("");
                welcomeTextView.setVisibility(View.GONE);
                setPromptEnabled(sendButton, editTextPrompt, false);


                // RISPOSTA?
                // TODO: AI reply
                String answer = getMessageAPI_WIP();
                Message messageAnswer = createMessage(conversationId, seq.getAndIncrement(),
                        false, answer);
                showMessage(messageAnswer, messageList, adapter);
                saveMessage(messageAnswer, getActivity());


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

    private void showMessage(Message message, ArrayList<Message> messageList,
                             MessageRecyclerAdapter adapter) {
        messageList.add(message);
        adapter.notifyItemInserted(messageList.size() -1);
    }

    private void sendMessage(Message message) {
        // chiamata API
    }

    private void saveMessage(Message message, Context context) {
        Log.d(TAG,"saveMessage");
        ServiceLocator.getInstance().getDao(requireActivity().getApplication()).messageDao().insert(message);

    }

    // TODO: dovrebbe ritornare la reply dell'AI
    private String getMessageAPI_WIP() {
        return "REPLY";
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

    private void populateChat(MessageRecyclerAdapter adapter, ArrayList<Message> messageList) {
        for(int i = 0; i < messageList.size() - 1; i++) {
            adapter.notifyDataSetChanged();
        }
    }

}