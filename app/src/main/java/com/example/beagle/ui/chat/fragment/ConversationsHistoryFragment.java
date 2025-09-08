package com.example.beagle.ui.chat.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.beagle.R;
import com.example.beagle.adapter.ConversationRecyclerAdapter;
import com.example.beagle.ui.chat.ChatActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.beagle.adapter.ConversationAdapter;
import com.example.beagle.model.Conversation;
import com.example.beagle.model.Message;
import com.example.beagle.util.Constants;
import com.example.beagle.util.ServiceLocator;

import java.util.ArrayList;
import java.util.List;

public class ConversationsHistoryFragment extends Fragment {

    public ConversationsHistoryFragment() { }

    public static ConversationsHistoryFragment newInstance(String param1, String param2) {
        ConversationsHistoryFragment fragment = new ConversationsHistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversations_history, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerConversations);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        /*
        // Dati dummy per test
        List<Conversation> demo = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (int i = 1; i <= 12; i++) {
            //Conversation c = new Conversation("conv-" + i, "pet-" + ((i % 3) + 1), now - i * 3600_000L);
            //Message m = new Message(c.getConversationId(), i, (i % 2 == 0), "Ultimo messaggio " + i);
            //c.addMessage(m);
            Conversation c = new Conversation(123, (i % 3) + 1);
            demo.add(c);
        }
         */

        long petId = 420;
        List<Conversation> demo = new ArrayList<>();
        demo.addAll(ServiceLocator.getInstance().getDao(requireActivity().getApplication()).conversationDao().getAll());

        ConversationRecyclerAdapter adapter = new ConversationRecyclerAdapter(R.layout.item_conversation, demo, new ConversationRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(Conversation conversation) {
                Bundle bundle = new Bundle();
                bundle.putLong(Constants.BUNDLE_KEY, conversation.getConversationId());
                //bundle.putParcelable("Id", conversation.getConversationId());
                Navigation.findNavController(view).navigate(R.id.action_conversationsHistoryFragment_to_chatFragment, bundle);
            }
        });

        recyclerView.setAdapter(adapter);

        /*
        ConversationAdapter adapter = new ConversationAdapter(demo, conversation -> {
            //String conversationId = conversation.getConversationId();
            //long conversationId = 1;
            String conversationId = "test";
            if (requireActivity() instanceof ChatActivity) {
                Bundle args = new Bundle();
                args.putString("conversationId", conversationId);
                androidx.navigation.fragment.NavHostFragment.findNavController(this)
                        .navigate(R.id.action_conversationsHistoryFragment_to_chatFragment, args);
            } else {
                Intent intent = new Intent(requireContext(), ChatActivity.class);
                intent.putExtra("conversationId", conversationId);
                startActivity(intent);
            }
        });
        recyclerConversations.setAdapter(adapter);

         */

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                menuInflater.inflate(R.menu.menu_conversations_history, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        requireActivity().setTitle(R.string.conversations_history_title);
    }
}