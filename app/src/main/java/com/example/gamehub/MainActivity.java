package com.example.gamehub;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.os.Build;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.example.gamehub.R;

import java.util.ArrayList;
import java.util.List;

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
        btnOpenDrawer = findViewById(R.id.btn_open_drawer);  // Certifique-se de que o ID está correto no XML
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

                if (id == R.id.nav_populares) {
                    // Implementar lógica para "Populares"
                    return true;
                } else if (id == R.id.nav_acao) {
                    // Implementar lógica para "Ação"
                    return true;
                } else if (id == R.id.nav_aventura) {
                    // Implementar lógica para "Aventura"
                    return true;
                } else if (id == R.id.nav_rpg) {
                    // Implementar lógica para "RPG"
                    return true;
                } else if (id == R.id.nav_estrategia) {
                    // Implementar lógica para "Estratégia"
                    return true;
                } else if (id == R.id.nav_terror) {
                    // Implementar lógica para "Terror"
                    return true;
                } else if (id == R.id.nav_plataforma) {
                    // Implementar lógica para "Plataforma"
                    return true;
                } else if (id == R.id.nav_corridas) {
                    // Implementar lógica para "Corridas"
                    return true;
                } else if (id == R.id.nav_puzzle) {
                    // Implementar lógica para "Puzzle"
                    return true;
                } else {
                    return false;
                }
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
        startActivityForResult(intent, 100);
    }

    // Lida com o resultado do reconhecimento de voz
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String appName = matches.get(0); // O primeiro resultado do reconhecimento de voz

            openApp(appName);
        }
    }

    private void openApp(String appName) {
        PackageManager packageManager = getPackageManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // Android 11 e superior
            // Filtrar apenas os pacotes visíveis à sua aplicação
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
            // Android 10 ou inferior
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
    // Adicione este método para obter a lista de jogos
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
