package com.example.nevos_shesh_besh;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {
    private List<GameRecord> games;

    public GameAdapter(List<GameRecord> games) { this.games = games; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameRecord g = games.get(position);
        holder.opponent.setText(g.getOpponentName());
        holder.winner.setText(g.getWinner());
        holder.type.setText(g.getWinType());
    }

    @Override
    public int getItemCount() { return games.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView opponent, winner, type;
        ViewHolder(View v) {
            super(v);
            opponent = v.findViewById(R.id.tv_row_opponent);
            winner = v.findViewById(R.id.tv_row_winner);
            type = v.findViewById(R.id.tv_row_type);
        }
    }
}