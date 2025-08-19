package com.example.beagle.ui.chat.fragment;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.beagle.R;
import com.example.beagle.adapter.MessageRecyclerAdapter;
import com.example.beagle.model.Message;
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
        //ArrayList<Message> answerList = new ArrayList<Message>();


        MessageRecyclerAdapter adapter = new MessageRecyclerAdapter(R.layout.message, messageList);
        recyclerView.setAdapter(adapter);


        // SET UP

        Resources res = getResources();
        String text = String.format(res.getString(R.string.saluto_iniziale), "amogus");

        TextView textView = view.findViewById(R.id.textView);
        textView.setText(text);

        editTextPrompt = view.findViewById(R.id.textInputPrompt);
        ImageButton sendButton = view.findViewById(R.id.imageSendButton);



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
                editTextPrompt.setError(res.getString(R.string.no_text));
            }
        });
    }
}