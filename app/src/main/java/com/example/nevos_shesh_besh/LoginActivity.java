package com.example.nevos_shesh_besh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etUser = findViewById(R.id.et_username_email);
        EditText etPass = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login_submit);
        Button btnGoToRegister = findViewById(R.id.btn_go_to_register);
        TextView tvForgotPass = findViewById(R.id.tv_go_to_register); // משמש ל"שכחת סיסמה" לפי ה-ID ב-XML

        // כפתור התחברות
        btnLogin.setOnClickListener(v -> {
            String user = etUser.getText().toString();
            String pass = etPass.getText().toString();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "מנסה להתחבר...", Toast.LENGTH_SHORT).show();
            }
        });

        // מעבר למסך הרשמה
        btnGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

        // כפתור שכחת סיסמה
        tvForgotPass.setOnClickListener(v -> {
            Toast.makeText(this, "שחזור סיסמה בבנייה...", Toast.LENGTH_SHORT).show();
        });
    }
}
