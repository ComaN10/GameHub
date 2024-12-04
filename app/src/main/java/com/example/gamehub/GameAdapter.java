package com.example.gamehub;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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

    public GameAdapter(Context context, List<ApplicationInfo> gamesList) {
        this.context = context;
        this.gamesList = gamesList;
        this.packageManager = context.getPackageManager();
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
        holder.gameName.setText(packageManager.getApplicationLabel(gameInfo));
        holder.gameIcon.setImageDrawable(packageManager.getApplicationIcon(gameInfo));

        holder.itemView.setOnClickListener(v -> {
            Intent launchIntent = packageManager.getLaunchIntentForPackage(gameInfo.packageName);
            if (launchIntent != null) {
                context.startActivity(launchIntent);
            }
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
