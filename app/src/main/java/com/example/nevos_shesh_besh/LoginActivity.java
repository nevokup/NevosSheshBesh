package com.example.nevos_shesh_besh;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText etInput = findViewById(R.id.et_username_email);
        EditText etPass = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login_submit);
        Button btnReg = findViewById(R.id.btn_go_to_register);
        Button btnBack = findViewById(R.id.btn_back); // כפתור חזרה

        // פונקציונליות חזרה
        btnBack.setOnClickListener(v -> finish());

        btnLogin.setOnClickListener(v -> {
            String input = etInput.getText().toString().trim();
            String pass = etPass.getText().toString();

            if (input.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
                return;
            }

            if (input.contains("@")) {
                if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                    etInput.setError("פורמט אימייל לא תקין");
                } else {
                    loginWithEmail(input, pass);
                }
            } else {
                findEmailAndLogin(input, pass);
            }
        });

        btnReg.setOnClickListener(v -> startActivity(new Intent(this, RegistrationActivity.class)));
    }

    private void findEmailAndLogin(String username, String password) {
        db.collection("users").whereEqualTo("username", username).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String email = "";
                        for (QueryDocumentSnapshot doc : task.getResult()) email = doc.getString("email");
                        loginWithEmail(email, password);
                    } else {
                        Toast.makeText(this, "שם משתמש לא זוהה במערכת", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException e) {
                            Toast.makeText(this, "אימייל לא זוהה במערכת", Toast.LENGTH_LONG).show();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            Toast.makeText(this, "סיסמה שגויה, נסה שוב", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(this, "שגיאה: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}