package com.example.nevos_shesh_besh;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etEmail, etUsername, etPassword, etConfirmPass, etAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        etEmail = findViewById(R.id.et_reg_email);
        etUsername = findViewById(R.id.et_reg_username);
        etPassword = findViewById(R.id.et_reg_password);
        etConfirmPass = findViewById(R.id.et_reg_confirm_password);
        etAge = findViewById(R.id.et_reg_age);
        Button btnRegister = findViewById(R.id.btn_register_submit);

        btnRegister.setOnClickListener(v -> {
            if (validateInput()) {
                // כאן תבוא הלוגיקה של Firebase בעתיד
                Toast.makeText(this, "הנתונים תקינים! עובר להרשמה...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput() {
        String email = etEmail.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPass = etConfirmPass.getText().toString();
        String age = etAge.getText().toString().trim();

        // בדיקת אימייל
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError(etEmail, "אימייל לא תקין");
            return false;
        }

        // בדיקת שם משתמש
        if (username.length() < 3) {
            showError(etUsername, "שם משתמש חייב להכיל לפחות 3 תווים");
            return false;
        }

        // בדיקת סיסמה
        if (password.length() < 6) {
            showError(etPassword, "סיסמה חייבת להכיל לפחות 6 תווים");
            return false;
        }

        // בדיקת התאמת סיסמאות
        if (!password.equals(confirmPass)) {
            showError(etConfirmPass, "הסיסמאות אינן תואמות");
            return false;
        }

        // בדיקת גיל
        if (age.isEmpty()) {
            showError(etAge, "נא להזין גיל");
            return false;
        }

        try {
            int ageVal = Integer.parseInt(age);
            if (ageVal < 1 || ageVal > 120) {
                showError(etAge, "גיל לא הגיוני");
                return false;
            }
        } catch (NumberFormatException e) {
            showError(etAge, "נא להזין מספר בלבד");
            return false;
        }

        return true;
    }

    private void showError(EditText input, String message) {
        input.setError(message);
        input.requestFocus();
    }
}