package com.example.nevos_shesh_besh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button btnStart = findViewById(R.id.btn_start_game);
        Button btnLeaderboard = findViewById(R.id.btn_leaderboard);
        Button btnAuth = findViewById(R.id.btn_auth);

        // כפתור התחל לשחק - מוביל למסך המשחק (MainActivity)
        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // כפתור לוח תוצאות - יחובב בהמשך למחלקה המתאימה
        btnLeaderboard.setOnClickListener(v -> {
            Toast.makeText(this, "דף לוח תוצאות בבנייה...", Toast.LENGTH_SHORT).show();
            // כאן יבוא Intent למסך ה-Leaderboard
        });

        btnAuth.setOnClickListener(v -> {
            // שיניתי מ-LoginActivity ל-Login כדי להתאים לשם המחלקה שתיקנו קודם
            Intent intent = new Intent(HomeActivity.this, Login.class);
            startActivity(intent);
        });
    }
}
