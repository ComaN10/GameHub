package com.example.gamehub;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ImageButton voiceSearchButton; // Botão de pesquisa por voz
    private ImageButton btnOpenDrawer; // Botão para abrir o menu lateral
    private DrawerLayout drawerLayout; // Layout do menu lateral
    private NavigationView navigationView; // Navegação do menu lateral
    private TextToSpeech textToSpeech; // Instância do Text-to-Speech
    private Button helpButton; // Botão para ajuda por voz

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialização dos componentes de UI
        voiceSearchButton = findViewById(R.id.voice_search_button);
        btnOpenDrawer = findViewById(R.id.btn_open_drawer);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        helpButton = findViewById(R.id.help_button);

        // Inicializar o Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.getDefault());
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Idioma não suportado no TTS.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Falha ao inicializar o TTS.", Toast.LENGTH_SHORT).show();
            }
        });

        // Configuração do clique no botão de ajuda
        helpButton.setOnClickListener(v -> speakInstructions());

        // Configuração do clique no botão de pesquisa por voz
        voiceSearchButton.setOnClickListener(v -> startVoiceRecognition());

        // Configuração do clique no botão de abrir o menu lateral
        btnOpenDrawer.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));

        // Configuração do menu lateral
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            String searchQuery = "";

            if (id == R.id.nav_populares) {
                searchQuery = "jogos populares";
            } else if (id == R.id.nav_acao) {
                searchQuery = "jogos de ação";
            } else if (id == R.id.nav_aventura) {
                searchQuery = "jogos de aventura";
            } else if (id == R.id.nav_rpg) {
                searchQuery = "jogos de RPG";
            } else if (id == R.id.nav_estrategia) {
                searchQuery = "jogos de estratégia";
            } else if (id == R.id.nav_terror) {
                searchQuery = "jogos de terror";
            } else if (id == R.id.nav_plataforma) {
                searchQuery = "jogos de plataforma";
            } else if (id == R.id.nav_corridas) {
                searchQuery = "jogos de corridas";
            } else if (id == R.id.nav_puzzle) {
                searchQuery = "jogos de puzzle";
            }

            if (!searchQuery.isEmpty()) {
                openGooglePlayStore(searchQuery);
            }
            drawerLayout.closeDrawers();
            return true;
        });

        // Configuração do RecyclerView para exibir jogos instalados
        RecyclerView recyclerView = findViewById(R.id.games_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obter e exibir os jogos instalados
        List<ApplicationInfo> installedGames = getInstalledGames();
        GameAdapter adapter = new GameAdapter(this, installedGames, this::speakGameName);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Fala as instruções de uso da aplicação.
     */
    private void speakInstructions() {
        String instructions = "Bem-vindo à GameHub. Aqui está como usar a aplicação. "
                + "Primeiro, você pode clicar no botão de pesquisa por voz para procurar jogos instalados. "
                + "Na lista de jogos, você pode clicar no nome de um jogo para ouvir o nome. "
                + "Se quiser abrir o jogo, pressione e segure o nome do jogo por mais de dois segundos. "
                + "Para ajuda, clique no botão de ajuda. "
                + "Esperamos que você aproveite a aplicação!";

        textToSpeech.speak(instructions, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    /**
     * Fala o nome do jogo selecionado usando Text-to-Speech.
     *
     * @param gameName Nome do jogo a ser falado.
     */
    private void speakGameName(String gameName) {
        if (textToSpeech != null) {
            textToSpeech.speak(gameName, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    /**
     * Inicia o reconhecimento de voz para comandos do usuário.
     */
    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        try {
            startActivityForResult(intent, 100);
        } catch (Exception e) {
            Toast.makeText(this, "Reconhecimento de voz não disponível.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && !matches.isEmpty()) {
                String userCommand = matches.get(0).toLowerCase();

                if (userCommand.contains("buscar") || userCommand.contains("procurar")) {
                    String location = userCommand.replace("buscar", "").replace("procurar", "").trim();
                    searchLocationOnMaps(location);
                } else {
                    openApp(userCommand);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    private void searchLocationOnMaps(String location) {
        if (location.isEmpty()) {
            Toast.makeText(this, "Por favor, especifique uma localização.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(location));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Toast.makeText(this, "O Google Maps não está instalado.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("GameHub", "Erro ao abrir o Google Maps: " + e.getMessage());
            Toast.makeText(this, "Erro ao abrir o Google Maps.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openApp(String appName) {
        PackageManager packageManager = getPackageManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);

            for (ResolveInfo resolveInfo : resolveInfos) {
                String appLabel = resolveInfo.loadLabel(packageManager).toString().toLowerCase();
                if (appLabel.contains(appName.toLowerCase())) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    launchApp(packageManager, packageName);
                    return;
                }
            }
        } else {
            List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            for (ApplicationInfo appInfo : installedApps) {
                String appLabel = packageManager.getApplicationLabel(appInfo).toString().toLowerCase();
                if (appLabel.contains(appName.toLowerCase())) {
                    launchApp(packageManager, appInfo.packageName);
                    return;
                }
            }
        }

        openGooglePlayStore(appName);
    }

    private void launchApp(PackageManager packageManager, String packageName) {
        Intent launchIntent = packageManager.getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(launchIntent);
        }
    }

    private void openGooglePlayStore(String query) {
        try {
            Uri uri = Uri.parse("market://search?q=" + Uri.encode(query));
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (goToMarket.resolveActivity(getPackageManager()) != null) {
                startActivity(goToMarket);
            } else {
                Toast.makeText(this, "Google Play Store não está instalado.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("GameHub", "Erro ao abrir Google Play Store: " + e.getMessage());
            Toast.makeText(this, "Erro ao abrir Google Play Store.", Toast.LENGTH_SHORT).show();
        }
    }

    private List<ApplicationInfo> getInstalledGames() {
        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> allApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        List<ApplicationInfo> games = new ArrayList<>();

        for (ApplicationInfo app : allApps) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && app.category == ApplicationInfo.CATEGORY_GAME) {
                games.add(app);
            }
        }
        return games;
    }
}
