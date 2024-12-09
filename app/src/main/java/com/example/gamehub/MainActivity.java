package com.example.gamehub;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private SpeechRecognizer speechRecognizer;
    private Button voiceSearchButton;
    private DrawerLayout drawerLayout;
    private ImageButton btnOpenDrawer;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializando os elementos de UI
        voiceSearchButton = findViewById(R.id.voice_search_button);
        btnOpenDrawer = findViewById(R.id.btn_open_drawer);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // Configurando o clique do botão de pesquisa por voz
        voiceSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognition();
            }
        });

        // Configurando o clique do botão para abrir o menu lateral
        btnOpenDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(navigationView);
            }
        });

        // Configuração do NavigationView
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                // Lógica para cada item do menu
                return true;
            }
        });

        // Configuração do RecyclerView
        RecyclerView recyclerView = findViewById(R.id.games_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtendo e configurando os jogos instalados
        List<ApplicationInfo> installedGames = getInstalledGames();
        GameAdapter adapter = new GameAdapter(this, installedGames);
        recyclerView.setAdapter(adapter);
    }

    // Inicia o reconhecimento de voz
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

    // Lida com o resultado do reconhecimento de voz
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String userCommand = matches.get(0).toLowerCase(); // Converte o comando para minúsculas

            // Decide se é um comando para abrir um jogo ou buscar no Google Maps
            if (userCommand.contains("buscar") || userCommand.contains("procurar")) {
                String location = userCommand.replace("buscar", "").trim();
                location = location.replace("procurar", "").trim();
                searchLocationOnMaps(location); // Chama o método para abrir o Google Maps
            } else {
                openApp(userCommand); // Método já implementado para abrir apps/jogos
            }
        }
    }

    // Método para buscar locais no Google Maps
    private void searchLocationOnMaps(String location) {
        if (location.isEmpty()) {
            Toast.makeText(this, "Por favor, diga o nome do local.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Cria uma URI para o Google Maps com a localização fornecida
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(location));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps"); // Garante que o Google Maps será usado

            // Verifica se o Google Maps está instalado
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Toast.makeText(this, "Google Maps não está instalado.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("GameHub", "Erro ao abrir o Google Maps: " + e.getMessage());
            Toast.makeText(this, "Erro ao abrir o Google Maps.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openApp(String appName) {
        PackageManager packageManager = getPackageManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // Android 11 e superior
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);

            for (ResolveInfo resolveInfo : resolveInfos) {
                String appLabel = resolveInfo.loadLabel(packageManager).toString().toLowerCase();
                if (appLabel.contains(appName.toLowerCase())) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    Intent launchIntent = packageManager.getLaunchIntentForPackage(packageName);
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(launchIntent);
                        return;
                    }
                }
            }
        } else {
            List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            for (ApplicationInfo appInfo : installedApps) {
                String appLabel = packageManager.getApplicationLabel(appInfo).toString().toLowerCase();
                if (appLabel.contains(appName.toLowerCase())) {
                    Intent launchIntent = packageManager.getLaunchIntentForPackage(appInfo.packageName);
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(launchIntent);
                        return;
                    }
                }
            }
        }

        // Caso a aplicação não seja encontrada, abra a Google Play Store
        Uri uri = Uri.parse("market://search?q=" + appName);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goToMarket);
    }

    private List<ApplicationInfo> getInstalledGames() {
        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> allApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        List<ApplicationInfo> games = new ArrayList<>();

        for (ApplicationInfo app : allApps) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0 &&
                    app.category == ApplicationInfo.CATEGORY_GAME) {
                games.add(app);
            }
        }
        return games;
    }
}
