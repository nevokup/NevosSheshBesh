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
// מחלקת מסך ההרשמה של האפליקציה, היורשת מ-AppCompatActivity.

    private EditText etEmail, etUsername, etPassword, etConfirmPass, etAge;
    // הגדרת שדות הקלט הגרפיים (תיבות טקסט) לקליטת נתוני המשתמש (אימייל, שם משתמש, סיסמאות וגיל).

    private FirebaseAuth mAuth;
    // רכיב ה-Authentication של Firebase האחראי על רישום וניהול חשבונות המשתמשים בענן.

    private FirebaseFirestore db;
    // רכיב בסיס הנתונים Firestore לשמירת המידע המשוייך למשתמש (שם וגיל) שלא נשמרים אוטומטית ברכיב ה-Auth.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // פונקציית האתחול של המסך. מקשרת את הרכיבים לקובץ ה-XML ומגדירה מאזיני לחיצה לכפתורים.
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
        Button btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());
        // בעת לחיצה על כפתור חזרה, המסך הנוכחי נסגר (יוצא מהמחסנית) והמשתמש חוזר למסך הקודם.

        btnRegister.setOnClickListener(v -> {
            if (validateInput()) {
                registerUser();
            }
        });
        // בעת לחיצה על כפתור הרשמה, מתבצעת קודם כל בדיקת תקינות קלט (ולקציה), ואם הכל תקין, מופעל תהליך הרישום.
    }

    private void registerUser() {
        // פונקציה המבצעת את הרישום בפועל: יוצרת משתמש ב-Firebase Auth, ובמידה והצליחה, שומרת את הפרטים הנוספים (שם, גיל) ב-Firestore תחת מזהה המשתמש הייחודי (UID).
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String username = etUsername.getText().toString().trim();
        String age = etAge.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();

                        Map<String, Object> user = new HashMap<>();
                        user.put("username", username);
                        user.put("age", Integer.parseInt(age));
                        user.put("email", email);

                        db.collection("users").document(userId)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(RegistrationActivity.this, "נרשמת בהצלחה!", Toast.LENGTH_SHORT).show();
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
        // פונקציה הבודקת את תקינות הנתונים שהוקלדו (אימייל בפורמט נכון, אורך סיסמה מעל 6 תווים, התאמה בין סיסמאות, ושדות לא ריקים). מחזירה True אם הכל תקין ו-False אם יש שגיאה.
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
        // פונקציית עזר המציגה שגיאה חזותית מובנית מעל שדה קלט ספציפי וממקדת את המקלדת אליו (Focus).
        input.setError(message);
        input.requestFocus();
    }
}