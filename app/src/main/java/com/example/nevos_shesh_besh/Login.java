package com.example.nevos_shesh_besh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etUser = findViewById(R.id.et_username_email);
        EditText etPass = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login_submit);
        Button btnGoToRegister = findViewById(R.id.btn_go_to_register);
        TextView tvForgotPass = findViewById(R.id.tv_go_to_register); // משמש כעת ל"שכחת סיסמה"

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

        // כפתור מעבר להרשמה החדש
        btnGoToRegister.setOnClickListener(v -> {
            Toast.makeText(this, "מעבר לדף הרשמה...", Toast.LENGTH_SHORT).show();
            // כאן יבוא Intent לדף הרשמה (Register) כשתבנה אותו
        });

        // כפתור שכחת סיסמה
        tvForgotPass.setOnClickListener(v -> {
            Toast.makeText(this, "שחזור סיסמה בבנייה...", Toast.LENGTH_SHORT).show();
        });
    }
}
