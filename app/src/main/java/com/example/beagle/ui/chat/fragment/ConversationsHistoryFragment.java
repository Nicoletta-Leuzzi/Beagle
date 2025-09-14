package com.example.beagle.ui.chat.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.beagle.R;
import com.example.beagle.adapter.ConversationRecyclerAdapter;

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

import com.example.beagle.database.DataRoomDatabase;
import com.example.beagle.model.Conversation;
import com.example.beagle.util.Constants;
import com.example.beagle.util.ServiceLocator;

import java.util.ArrayList;
import java.util.List;

public class ConversationsHistoryFragment extends Fragment {

    public ConversationsHistoryFragment() { }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversations_history, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerConversations);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));


        long petId = 420;
        //demo.addAll(ServiceLocator.getInstance().getDao(requireActivity().getApplication()).conversationDao().getAll());
        List<Conversation> conversationList = new ArrayList<>(ServiceLocator.getInstance().getDao(requireActivity().getApplication()).conversationDao().getConversations(petId));

        ConversationRecyclerAdapter adapter = new ConversationRecyclerAdapter(R.layout.item_conversation, conversationList, new ConversationRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(Conversation conversation) {
                Bundle bundle = new Bundle();
                bundle.putLong(Constants.CONVERSATION_BUNDLE_KEY, conversation.getConversationId());

                Navigation.findNavController(view).navigate(R.id.action_conversationsHistoryFragment_to_chatFragment, bundle);
            }
        });

        recyclerView.setAdapter(adapter);

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