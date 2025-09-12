package com.example.beagle.ui.chat;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.beagle.R;
import com.google.android.material.appbar.MaterialToolbar;


public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainterViewChat);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            // If Activity was started with a conversationId extra, inject it as default args
            String conversationId = getIntent() != null
                    ? getIntent().getStringExtra("conversationId")
                    : null;
            if (conversationId != null) {
                NavInflater inflater = navController.getNavInflater();
                NavGraph graph = inflater.inflate(R.navigation.chat_nav_graph);
                Bundle args = new Bundle();
                args.putString("conversationId", conversationId);
                navController.setGraph(graph, args);
            }

            AppBarConfiguration appBarConfiguration =
                    new AppBarConfiguration.Builder(navController.getGraph()).build();
            NavigationUI.setupWithNavController(topAppBar, navController, appBarConfiguration);
        }
    }
}