package com.example.beagle.ui.chat.fragment;

import static com.example.beagle.util.Constants.SHARED_PREFERENCES_ACTIVE_PET_ID;
import static com.example.beagle.util.Constants.SHARED_PREFERENCES_ACTIVE_PET_NAME;
import static com.example.beagle.util.Constants.SHARED_PREFERENCES_FILENAME;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
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
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.beagle.model.Conversation;
import com.example.beagle.model.Result;
import com.example.beagle.repository.conversation.ConversationRepository;
import com.example.beagle.ui.chat.viewmodel.conversation.ConversationViewModel;
import com.example.beagle.ui.chat.viewmodel.conversation.ConversationViewModelFactory;
import com.example.beagle.util.Constants;
import com.example.beagle.util.ServiceLocator;
import com.example.beagle.util.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

public class ConversationsHistoryFragment extends Fragment {

    private long petId;
    private SharedPreferencesUtils sharedPreferencesUtils;
    private MutableLiveData<Result> conversationReadLiveData;
    private List<Conversation> conversationList;
    private ConversationViewModel conversationViewModel;
    private ConversationRecyclerAdapter adapter;



    public ConversationsHistoryFragment() { }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferencesUtils = new SharedPreferencesUtils(requireActivity().getApplication());

        if (sharedPreferencesUtils.readStringData(
                SHARED_PREFERENCES_FILENAME, SHARED_PREFERENCES_ACTIVE_PET_ID) != null) {
            String petIdString = sharedPreferencesUtils.readStringData(SHARED_PREFERENCES_FILENAME,
                    SHARED_PREFERENCES_ACTIVE_PET_ID);
            petId = Long.parseLong(petIdString);
        } else {
            petId = 0;
        }

        ConversationRepository conversationRepository =
                ServiceLocator.getInstance().getConversationRepository(
                        requireActivity().getApplication()
                );

        conversationViewModel = new ViewModelProvider(
                requireActivity(),
                new ConversationViewModelFactory(conversationRepository))
                .get(ConversationViewModel.class);

        conversationList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversations_history, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerConversations);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));


        //long petId = 420;
        //demo.addAll(ServiceLocator.getInstance().getDao(requireActivity().getApplication()).conversationDao().getAll());
        //List<Conversation> conversationList = new ArrayList<>(ServiceLocator.getInstance().getDao(requireActivity().getApplication()).conversationDao().getConversations(petId));

        adapter = new ConversationRecyclerAdapter(R.layout.item_conversation, conversationList,
                new ConversationRecyclerAdapter.OnItemClickListener() {
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


        Log.d("plop", "TEST");
        Log.d("plop", "petId: " + petId);


        conversationReadLiveData = conversationViewModel.getConversations(petId, false);
        conversationReadLiveData.observe(getViewLifecycleOwner(),
                    result -> {
                        if (result.isSuccess()) {
                            Log.d("plop", "INSIDE OBSERVER");
                            conversationList.clear();
                            conversationList.addAll(((Result.ConversationSuccess) result).getData());
                            adapter.notifyDataSetChanged();
                        }
                    });


        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                menuInflater.inflate(R.menu.menu_conversations_history, menu);
            }



            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_new_chat) {
                    Navigation.findNavController(view)
                            .navigate(R.id.action_conversationsHistoryFragment_to_chatFragment);
                }
                if (menuItem.getItemId() == R.id.action_pet_profile) {
                    Navigation.findNavController(view)
                            .navigate(R.id.action_conversationsHistoryFragment_to_profileFragment);
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        requireActivity().setTitle(R.string.conversations_history_title);
    }
}