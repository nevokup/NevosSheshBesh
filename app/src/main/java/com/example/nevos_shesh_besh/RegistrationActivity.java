package com.example.nevos_shesh_besh;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etEmail, etUsername, etPassword, etConfirmPass, etAge;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.et_reg_email);
        etUsername = findViewById(R.id.et_reg_username);
        etPassword = findViewById(R.id.et_reg_password);
        etConfirmPass = findViewById(R.id.et_reg_confirm_password);
        etAge = findViewById(R.id.et_reg_age);
        Button btnRegister = findViewById(R.id.btn_register_submit);

        btnRegister.setOnClickListener(v -> {
            if (validateInput()) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String username = etUsername.getText().toString().trim();
        String age = etAge.getText().toString().trim();

        // יצירת משתמש ב-Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();

                        // שמירת נתונים נוספים ב-Firestore
                        Map<String, Object> user = new HashMap<>();
                        user.put("username", username);
                        user.put("age", Integer.parseInt(age));
                        user.put("email", email);

                        db.collection("users").document(userId)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(RegistrationActivity.this, "נרשמת בהצלחה!", Toast.LENGTH_SHORT).show();
                                    // מעבר לדף הלוג-אין
                                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(RegistrationActivity.this, "שגיאה בשמירת נתונים: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(RegistrationActivity.this, "שגיאת הרשמה: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validateInput() {
        String email = etEmail.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPass = etConfirmPass.getText().toString();
        String age = etAge.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError(etEmail, "אימייל לא תקין");
            return false;
        }
        if (username.length() < 3) {
            showError(etUsername, "שם משתמש חייב להכיל לפחות 3 תווים");
            return false;
        }
        if (password.length() < 6) {
            showError(etPassword, "סיסמה חייבת להכיל לפחות 6 תווים");
            return false;
        }
        if (!password.equals(confirmPass)) {
            showError(etConfirmPass, "הסיסמאות אינן תואמות");
            return false;
        }
        if (age.isEmpty()) {
            showError(etAge, "נא להזין גיל");
            return false;
        }
        return true;
    }

    private void showError(EditText input, String message) {
        input.setError(message);
        input.requestFocus();
    }
}