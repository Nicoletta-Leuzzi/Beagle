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

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    private TextInputEditText editTextPrompt;
    private String messageContent;
    private String petName;


    //private Pet pet;
    //private TextView questionMessage = findViewById(R.id.textViewQuestion);

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


        // TODO: Da rimuovere dopo con effettiva ricerca di animale
        Pet pet = new Pet("Among Us");

        // SET UP

        petName = pet.getName();
        editTextPrompt = view.findViewById(R.id.textInputPrompt);
        Button addPetButton = view.findViewById(R.id.addPetButton);
        ImageButton sendButton = view.findViewById(R.id.imageSendButton);
        TextView textView = view.findViewById(R.id.textView);


        Resources res = getResources();


        String firstText;
        if (hasPetSaved()) {
            //firstText = String.format(Constants.WELCOME_MESSAGE, petName);
            firstText = String.format(res.getString(R.string.saluto_iniziale), petName);
            sendButton.setEnabled(true);
            addPetButton.setVisibility(View.GONE);
        } else {
            //firstText = Constants.NO_PET_MESSAGE;
            firstText = String.format(res.getString(R.string.no_pet));
            sendButton.setEnabled(false);
            addPetButton.setVisibility(View.VISIBLE);
        }
        textView.setText(firstText);

        // TODO: pulire (e finire) codice
        sendButton.setOnClickListener(v -> {
            if (!Objects.requireNonNull(editTextPrompt.getText()).toString().trim().isEmpty()) {
                // CREA MESSAGGIO E LO MOSTRA
                messageContent = editTextPrompt.getText().toString();
                Message messageQuestion = new Message(messageContent, true);
                messageList.add(messageQuestion);
                adapter.notifyItemInserted(messageList.size());

                // RISPOSTA?

                Message messageAnswer = new Message("REPLY", false);
                messageList.add(messageAnswer);
                adapter.notifyItemInserted(messageList.size());


                editTextPrompt.setText("");
                textView.setText("");

            } else {
                //editTextPrompt.setError(Constants.NO_MESSAGE);
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


    // TODO: da spostare
    public boolean hasPetSaved() {
        return true;
    }


}