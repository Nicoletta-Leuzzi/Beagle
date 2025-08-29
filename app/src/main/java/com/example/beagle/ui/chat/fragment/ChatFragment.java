package com.example.beagle.ui.chat.fragment;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.beagle.model.Message;
import com.example.beagle.model.Pet;
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    // private String messageContent;
    // TODO: Da rimuovere dopo con ultimo animale se esiste (e mettere in luogo più adeguato)
    Pet pet = new Pet("Among Us");




    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // RECYCLER ADAPTER STUFF
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        ArrayList<Message> messageList = new ArrayList<Message>();

        MessageRecyclerAdapter adapter = new MessageRecyclerAdapter(R.layout.message, messageList);
        recyclerView.setAdapter(adapter);




        // SET UP
        TextInputEditText editTextPrompt = view.findViewById(R.id.textInputPrompt);
        Button addPetButton = view.findViewById(R.id.addPetButton);
        ImageButton sendButton = view.findViewById(R.id.imageSendButton);
        TextView welcomeTextView = view.findViewById(R.id.textView);

        Resources res = getResources();

        // Se esiste un animale salvato, forma screen normale
        if (hasPetSaved()) {
            welcomeTextView.setText(String.format(res.getString(R.string.saluto_iniziale),
                    pet.getName()));
            addPetButton.setVisibility(View.GONE);
            setPromptEnabled(sendButton, editTextPrompt, true);

        // Altrimenti, forma screen con bottone aggiungi animale
        } else {
            welcomeTextView.setText(String.format(res.getString(R.string.no_pet)));
            addPetButton.setVisibility(View.VISIBLE);
            setPromptEnabled(sendButton, editTextPrompt, false);
        }


        addPetButton.setOnClickListener(v -> {
            // TODO: intent verso Pet activity
        });


        // TODO: finire codice
        sendButton.setOnClickListener(v -> {
            // Se il prompt non è vuoto
            if (!Objects.requireNonNull(editTextPrompt.getText()).toString().trim().isEmpty()) {

                // CREA MESSAGGIO E LO MOSTRA
                String messageContent = editTextPrompt.getText().toString();
                sendMessage(messageContent, messageList, adapter, true);
                editTextPrompt.setText("");
                welcomeTextView.setVisibility(View.GONE);
                setPromptEnabled(sendButton, editTextPrompt, false);

                // RISPOSTA?
                // TODO: AI reply
                String answer = getMessageAPI_WIP();
                sendMessage(answer, messageList, adapter, false);
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
                    return true;
                }
                return false;
            }

        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }




    // TODO: da spostare tutti i metodi in classe(i) più adeguata(e)

    // TODO: dovrebbe ritornare la reply dell'AI
    private String getMessageAPI_WIP() {
        return "REPLY";
    }

    // Mostra il messaggio in Chat
    private void sendMessage(String messageContent, ArrayList<Message> messageList,
                             MessageRecyclerAdapter adapter, boolean fromUser) {
        Message message = new Message(messageContent, fromUser);
        messageList.add(message);
        adapter.notifyItemInserted(messageList.size());
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