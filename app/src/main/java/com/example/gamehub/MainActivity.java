package com.example.gamehub;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private ArrayList<String> gameList;
    private GameAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.game_list);
        Button voiceSearchButton = findViewById(R.id.voice_search_button);

        // Lista de jogos
        gameList = new ArrayList<>(Arrays.asList("The Witcher 3", "Minecraft", "Elden Ring", "Cyberpunk 2077", "God of War"));

        // Configurar o adaptador
        adapter = new GameAdapter(this, gameList);
        listView.setAdapter(adapter);

        // Botão para ativar pesquisa por voz
        voiceSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognition();
            }
        });
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-PT");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "O reconhecimento de voz não está disponível!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                filterGames(result.get(0));
            }
        }
    }

    private void filterGames(String query) {
        ArrayList<String> filteredList = new ArrayList<>();
        for (String game : gameList) {
            if (game.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(game);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Nenhum jogo encontrado!", Toast.LENGTH_SHORT).show();
        } else {
            adapter.updateList(filteredList);
        }
    }
}
