package com.example.nevos_shesh_besh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvWelcome;
    private Button btnAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvWelcome = findViewById(R.id.tv_welcome_user);
        btnAuth = findViewById(R.id.btn_auth);
        Button btnStart = findViewById(R.id.btn_start_game);
        Button btnLeaderboard = findViewById(R.id.btn_leaderboard);

        btnLeaderboard.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                showLoginRequiredDialog();
            } else {
                Intent intent = new Intent(HomeActivity.this, LeaderboardActivity.class);
                startActivity(intent);
            }
        });

        btnStart.setOnClickListener(v -> showGameModeDialog());

        btnAuth.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() != null) {
                mAuth.signOut();
                checkUserStatus();
                Toast.makeText(this, "התנתקת בהצלחה", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        checkUserStatus();
    }

    private void showGameModeDialog() {
        String[] options = {"צור משחק", "הצטרף למשחק", "משחק יחיד (מקומי)"};
        new AlertDialog.Builder(this)
                .setTitle("בחר מצב משחק")
                .setItems(options, (dialog, which) -> {
                    if (which == 2) {
                        // משחק יחיד - תמיד מותר
                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        intent.putExtra("mode", "local");
                        startActivity(intent);
                    } else {
                        // צור/הצטרף - דורש התחברות
                        if (mAuth.getCurrentUser() == null) {
                            showLoginRequiredDialog();
                        } else {
                            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                            intent.putExtra("mode", which == 0 ? "create" : "join");
                            startActivity(intent);
                        }
                    }
                })
                .show();
    }

    private void showLoginRequiredDialog() {
        new AlertDialog.Builder(this)
                .setTitle("נדרשת התחברות")
                .setMessage("עליך להתחבר למערכת כדי להשתמש באפשרות זו.")
                .setPositiveButton("להתחברות", (dialog, which) -> {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                })
                .setNegativeButton("ביטול", null)
                .show();
    }

    private void checkUserStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            btnAuth.setText("התנתק");
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            String name = doc.getString("username");
                            tvWelcome.setText("שלום " + name + "!");
                            tvWelcome.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            btnAuth.setText("כניסה/הרשמה");
            tvWelcome.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }
}
