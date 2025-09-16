package com.example.beagle.ui.chat.fragment;

import android.os.Bundle;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.beagle.R;
import com.example.beagle.adapter.ConversationRecyclerAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.beagle.model.Conversation;
import com.example.beagle.model.Result;
import com.example.beagle.ui.chat.viewmodel.conversation.ConversationViewModel;
import com.example.beagle.ui.chat.viewmodel.conversation.ConversationViewModelFactory;
import com.example.beagle.util.Constants;
import com.example.beagle.util.ServiceLocator;
import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

public class ConversationsHistoryFragment extends Fragment {

    private static final long DEFAULT_PET_ID = 420L;

    private final List<Conversation> conversationList = new ArrayList<>();
    private ConversationRecyclerAdapter adapter;
    private ConversationViewModel conversationViewModel;
    private long petId = DEFAULT_PET_ID;

    public ConversationsHistoryFragment() { }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConversationViewModelFactory factory = new ConversationViewModelFactory(
                ServiceLocator.getInstance().getConversationRepository(requireActivity().getApplication())
        );
        conversationViewModel = new ViewModelProvider(this, factory)
                .get(ConversationViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversations_history, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerConversations);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter = new ConversationRecyclerAdapter(
                R.layout.item_conversation,
                conversationList,
                conversation -> {
                    NavController navController = Navigation.findNavController(view);
                    NavBackStackEntry previousEntry = navController.getPreviousBackStackEntry();
                    if (previousEntry != null) {
                        previousEntry.getSavedStateHandle()
                                .set(Constants.CONVERSATION_BUNDLE_KEY, conversation.getConversationId());
                    }
                    navController.popBackStack();
                },
                (anchor, conversation) -> showConversationMenu(anchor, conversation)
        );

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
                // TODO: gestire menu_item_conversation
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        requireActivity().setTitle(R.string.conversations_history_title);

        observeConversations();
    }

    private void observeConversations() {
        conversationViewModel.getConversations(petId, false).observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.ConversationSuccess) {
                conversationList.clear();
                conversationList.addAll(((Result.ConversationSuccess) result).getData());
                adapter.notifyDataSetChanged();
            } else if (result instanceof Result.Error) {
                View root = getView();
                if (root != null) {
                    Snackbar.make(root, ((Result.Error) result).getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showConversationMenu(View anchor, Conversation conversation) {
        PopupMenu popupMenu = new PopupMenu(anchor.getContext(), anchor, Gravity.END);
        popupMenu.inflate(R.menu.menu_item_conversation);
        popupMenu.setForceShowIcon(true);
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.delete_conversation) {
                deleteConversation(conversation);
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void deleteConversation(Conversation conversation) {
        conversationViewModel.deleteConversation(conversation, petId);
        View view = getView();
        if (view == null) {
            return;
        }
        NavController navController = Navigation.findNavController(view);
        NavBackStackEntry previousEntry = navController.getPreviousBackStackEntry();
        if (previousEntry != null) {
            previousEntry.getSavedStateHandle()
                    .set(Constants.CONVERSATION_DELETED_BUNDLE_KEY, conversation.getConversationId());
        }
    }

}
