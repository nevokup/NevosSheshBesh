package com.example.nevos_shesh_besh;
// שיוך החבילה של האפליקציה.

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
// ייבוא (Import) של כל הספריות, רכיבי ה-UI, והרכיבים של Firebase הנדרשים למסך זה.

public class LoginActivity extends AppCompatActivity {
// הגדרת מחלקת מסך ההתחברות, היורשת מ-AppCompatActivity (מחלקת בסיס למסכים באנדרואיד).

    private FirebaseAuth mAuth;
    // רכיב ה-Authentication של Firebase האחראי על תהליך זיהוי, כניסה וניתוק משתמשים.

    private FirebaseFirestore db;
    // רכיב בסיס הנתונים (Firestore) שבו שמור המידע הנוסף על המשתמשים (כמו שמות משתמש).

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // פונקציית מחזור החיים הראשונה שרצה כשהמסך נוצר ומורם לזיכרון.

        super.onCreate(savedInstanceState);
        // קריאה לפונקציית האבא כדי לבצע את פעולות היצירה הבסיסיות של אנדרואיד.

        setContentView(R.layout.activity_login);
        // חיבור קובץ ה-XML העיצובי של מסך ההתחברות למחלקת הג'אווה הזו.

        mAuth = FirebaseAuth.getInstance();
        // אתחול רכיב האימות - קבלת מופע (Instance) פעיל של מערכת ה-Auth בענן.

        db = FirebaseFirestore.getInstance();
        // אתחול רכיב בסיס הנתונים - קבלת מופע פעיל של ה-Firestore בענן.

        EditText etInput = findViewById(R.id.et_username_email);
        // מציאת שדה קלט הטקסט מה-XML שבו המשתמש מזין את האימייל או את שם המשתמש שלו.

        EditText etPass = findViewById(R.id.et_password);
        // מציאת שדה קלט הטקסט מה-XML שבו המשתמש מזין את הסיסמה שלו.

        Button btnLogin = findViewById(R.id.btn_login_submit);
        // מציאת כפתור שליחת הטופס והתחברות.

        Button btnReg = findViewById(R.id.btn_go_to_register);
        // מציאת הכפתור שמעביר את המשתמש למסך ההרשמה (אם אין לו חשבון).

        Button btnBack = findViewById(R.id.btn_back);
        // מציאת כפתור החזרה למסך הקודם.

        btnBack.setOnClickListener(v -> finish());
        // הגדרת מאזין לחיצה לכפתור החזרה. הפקודה finish() סוגרת את המסך הנוכחי ומחזירה אותנו למסך הקודם (מסך הבית).

        btnLogin.setOnClickListener(v -> {
            // הגדרת מאזין לחיצה לכפתור ההתחברות. כל מה שבתוך הסוגריים יקרה ברגע שהמשתמש ילחץ עליו.

            String input = etInput.getText().toString().trim();
            // שליפת הטקסט שהמשתמש הקליד בשדה האימייל/שם משתמש, הפיכתו למחרוזת, ומחיקת רווחים מיותרים מהקצוות בעזרת trim().

            String pass = etPass.getText().toString();
            // שליפת הסיסמה שהמשתמש הקליד.

            if (input.isEmpty() || pass.isEmpty()) {
                // בדיקת תקינות: אם אחד השדות (או שניהם) נשארו ריקים:

                Toast.makeText(this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
                // הצגת הודעת טקסט קופצת קצרה (Toast) המבקשת למלא את הנתונים.

                return;
                // עצירת המשך הפונקציה (לא ננסה להתחבר אם השדות ריקים).
            }

            if (input.contains("@")) {
                // בדיקה לוגית: אם הקלט מכיל את הסימן @, סימן שהמשתמש מנסה להתחבר באמצעות אימייל.

                if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                    // שימוש בכלי מובנה באנדרואיד (Patterns) לבדיקה האם מבנה האימייל תקין וחוקי. אם הוא לא תקין:

                    etInput.setError("פורמט אימייל לא תקין");
                    // הצגת סימן שגיאה אדום בתוך שדה הקלט עצמו.
                } else {
                    loginWithEmail(input, pass);
                    // אם פורמט האימייל תקין, קריאה לפונקציה שמחברת את המשתמש ישירות עם אימייל וסיסמה.
                }
            } else {
                findEmailAndLogin(input, pass);
                // אם הקלט לא מכיל @, סימן שהמשתמש הזין את שם המשתמש שלו (Username). נקרא לפונקציה שמחפשת את האימייל שלו בענן.
            }
        });

        btnReg.setOnClickListener(v -> startActivity(new Intent(this, RegistrationActivity.class)));
        // הגדרת מאזין לכפתור הרשמה: יצירת כוונה (Intent) למעבר ומעבר בפועל למסך הרישום באמצעות startActivity.
    }

    private void findEmailAndLogin(String username, String password) {
        // פונקציה שמקבלת שם משתמש וסיסמה, ומחפשת את האימייל המתאים ב-Firestore.

        db.collection("users").whereEqualTo("username", username).get()
                // פנייה לאוסף (Collection) שנקרא "users" בבסיס הנתונים, וביצוע שאילתה (Query): "תביא לי את המסמך שבו השדה username שווה בדיוק לשם שהוזן". לאחר מכן מפעילים .get() להבאת הנתונים.

                .addOnCompleteListener(task -> {
                    // רישום מאזין שיודע מתי פעולת החיפוש בענן הסתיימה (בין אם הצליחה ובין אם נכשלה).

                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // אם השאילתה הצליחה, ונמצא לפחות מסמך אחד התואם לשם המשתמש הזה:

                        String email = "";
                        // משתנה זמני לשמירת האימייל שנמצא.

                        for (QueryDocumentSnapshot doc : task.getResult()) email = doc.getString("email");
                        // לולאת For-Each שעוברת על תוצאות החיפוש (במקרה שלנו יהיה מסמך אחד) ושולפת מתוכו את הערך של השדה "email".

                        loginWithEmail(email, password);
                        // כעת, כשיש לנו את האימייל האמיתי של המשתמש, נקרא לפונקציית ההתחברות הרגילה.
                    } else {
                        Toast.makeText(this, "שם משתמש לא זוהה במערכת", Toast.LENGTH_SHORT).show();
                        // אם לא נמצא שום מסמך עם שם משתמש כזה, נציג הודעה מתאימה.
                    }
                });
    }

    private void loginWithEmail(String email, String password) {
        // פונקציה שמבצעת את ההתחברות הפיזית מול שרת ה-Authentication של Firebase באמצעות אימייל וסיסמה.

        mAuth.signInWithEmailAndPassword(email, password)
                // פקודת מפתח של Firebase לביצוע התחברות. היא שולחת את האימייל והסיסמה לענן לבדיקה.

                .addOnCompleteListener(task -> {
                    // מאזין שממתין לתשובה משרתי Firebase האם החיבור הצליח או נכשל.

                    if (task.isSuccessful()) {
                        // אם השרת אישר שהפרטים נכונים וההתחברות הצליחה:

                        finish();
                        // סגירת מסך ההתחברות הנוכחי וחזרה אוטומטית למסך הבית (כאשר המשתמש כעת כבר רשום כמחובר במערכת).
                    } else {
                        // אם חזרה תשובה שלילית מהשרת, נשתמש במנגנון Try-Catch כדי להבין בדיוק מה השתבש:

                        try {
                            throw task.getException();
                            // "נזרוק" בכוונה את השגיאה שחזרה מהשרת כדי לתפוס אותה בבלוקים של ה-catch הבאים.
                        } catch (FirebaseAuthInvalidUserException e) {
                            // מקרה 1: נתפסה שגיאה מסוג "משתמש לא קיים" (אימייל לא רשום).

                            Toast.makeText(this, "אימייל לא זוהה במערכת", Toast.LENGTH_LONG).show();
                            // נציג הודעה שהאימייל לא קיים.
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            // מקרה 2: נתפסה שגיאה מסוג "פרטים לא תקפים" (אימייל נכון, אך סיסמה שגויה).

                            Toast.makeText(this, "סיסמה שגויה, נסה שוב", Toast.LENGTH_LONG).show();
                            // נציג הודעה שהסיסמה שגויה.
                        } catch (Exception e) {
                            // מקרה 3: כל שגיאה כללית אחרת (כמו חוסר באינטרנט וכדומה).

                            Toast.makeText(this, "שגיאה: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            // נציג את הודעת השגיאה הכללית שחזרה.
                        }
                    }
                });
    }
}