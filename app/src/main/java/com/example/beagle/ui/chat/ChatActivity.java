package com.example.beagle.ui.chat;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.beagle.R;
import com.example.beagle.util.PreferencesManager;
import com.google.android.material.appbar.MaterialToolbar;


public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        PreferencesManager prefs = new PreferencesManager(this);

        // TEMA
        int savedTheme = prefs.getTheme();
        if (savedTheme != -1) {
            AppCompatDelegate.setDefaultNightMode(savedTheme);
        }

        // LINGUA
        String savedLang = prefs.getLanguage();
        if (!savedLang.isEmpty()) {
            LocaleListCompat locale = LocaleListCompat.forLanguageTags(savedLang);
            AppCompatDelegate.setApplicationLocales(locale);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerViewChat);

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