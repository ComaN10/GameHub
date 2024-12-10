package com.example.gamehub;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private Context context;
    private List<ApplicationInfo> gamesList;
    private PackageManager packageManager;
    private OnGameClickListener onGameClickListener; // Interface para clique curto

    // Interface para callback de fala do nome do jogo
    public interface OnGameClickListener {
        void onGameNameSpeak(String gameName);
    }

    public GameAdapter(Context context, List<ApplicationInfo> gamesList, OnGameClickListener listener) {
        this.context = context;
        this.gamesList = gamesList;
        this.packageManager = context.getPackageManager();
        this.onGameClickListener = listener;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.game_item, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        ApplicationInfo gameInfo = gamesList.get(position);
        String gameName = packageManager.getApplicationLabel(gameInfo).toString();

        // Define o nome e ícone do jogo
        holder.gameName.setText(gameName);
        holder.gameIcon.setImageDrawable(packageManager.getApplicationIcon(gameInfo));

        // Configuração do clique curto
        holder.itemView.setOnClickListener(v -> {
            if (onGameClickListener != null) {
                onGameClickListener.onGameNameSpeak(gameName); // Falar o nome do jogo
            }
        });

        // Configuração do clique longo (mais de 2 segundos)
        holder.itemView.setOnLongClickListener(v -> {
            Intent launchIntent = packageManager.getLaunchIntentForPackage(gameInfo.packageName);
            if (launchIntent != null) {
                context.startActivity(launchIntent); // Abre o jogo
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return gamesList.size();
    }

    public static class GameViewHolder extends RecyclerView.ViewHolder {
        TextView gameName;
        ImageView gameIcon;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            gameName = itemView.findViewById(R.id.game_name);
            gameIcon = itemView.findViewById(R.id.game_icon);
        }
    }
}
