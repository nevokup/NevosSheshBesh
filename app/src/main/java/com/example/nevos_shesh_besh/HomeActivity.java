package com.example.nevos_shesh_besh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

        // יציאה מהחשבון בריענון כדי לשמור על המצב ה"רגיל"
        mAuth.signOut();

        tvWelcome = findViewById(R.id.tv_welcome_user);
        btnAuth = findViewById(R.id.btn_auth);
        Button btnStart = findViewById(R.id.btn_start_game);
        Button btnLeaderboard = findViewById(R.id.btn_leaderboard);

        btnStart.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        btnAuth.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));

        checkUserStatus();
    }

    private void checkUserStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            btnAuth.setVisibility(View.GONE);
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            String name = doc.getString("username");
                            tvWelcome.setText("שלום " + name + "!");
                            tvWelcome.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            btnAuth.setVisibility(View.VISIBLE);
            tvWelcome.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }
}