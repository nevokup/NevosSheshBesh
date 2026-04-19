package com.example.nevos_shesh_besh;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    private TextView tvPlayerName, tvTotal, tvTotalWins, tvWinRegular, tvWinMars, tvWinStars, tvLosses;
    private GameAdapter adapter;
    private List<GameRecord> gameList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        db = FirebaseFirestore.getInstance();
        
        tvPlayerName = findViewById(R.id.tv_player_name_summary);
        tvTotal = findViewById(R.id.tv_total_games);
        tvTotalWins = findViewById(R.id.tv_total_wins_count);
        tvWinRegular = findViewById(R.id.tv_win_type_regular);
        tvWinMars = findViewById(R.id.tv_win_type_mars);
        tvWinStars = findViewById(R.id.tv_win_type_stars);
        tvLosses = findViewById(R.id.tv_losses_count);

        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rv_history);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GameAdapter(gameList);
        rv.setAdapter(adapter);

        loadUserData();
        loadGamesData();
    }

    private void loadUserData() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("username");
                        if (name != null) {
                            tvPlayerName.setText(name);
                        }
                    }
                });
    }

    private void loadGamesData() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        db.collection("games")
                .whereEqualTo("playerUid", uid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value == null) return;
                    
                    gameList.clear();
                    int total = 0, reg = 0, mars = 0, stars = 0;

                    for (QueryDocumentSnapshot doc : value) {
                        GameRecord g = doc.toObject(GameRecord.class);
                        gameList.add(g);
                        total++;
                        
                        if ("אני".equals(g.getWinner())) {
                            String type = g.getWinType();
                            if ("רגיל".equals(type)) reg++;
                            else if ("מרס".equals(type)) mars++;
                            else if ("מרס כוכבים".equals(type)) stars++;
                        }
                    }

                    int winsCount = reg + mars + stars;
                    tvTotal.setText("משחקים: " + total);
                    tvTotalWins.setText(String.valueOf(winsCount));
                    tvWinRegular.setText("• ניצחון רגיל: " + reg);
                    tvWinMars.setText("• ניצחון מארס: " + mars);
                    tvWinStars.setText("• ניצחון מארס כוכבים: " + stars);
                    tvLosses.setText("הפסדים: " + (total - winsCount));
                    
                    adapter.notifyDataSetChanged();
                });
    }
}
