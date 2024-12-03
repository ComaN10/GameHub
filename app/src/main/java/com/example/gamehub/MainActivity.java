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
import android.view.View;
import android.widget.Button;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SpeechRecognizer speechRecognizer;
    private Button voiceSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        voiceSearchButton = findViewById(R.id.voice_search_button);

        voiceSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognition();
            }
        });
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


}
