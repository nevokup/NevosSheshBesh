package com.example.nevos_shesh_besh;
// שיוך החבילה של האפליקציה.

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
// ייבוא מחלקות גרפיות ומחלקות התקשורת של Firebase.

public class HomeActivity extends AppCompatActivity {
// הגדרת מחלקת מסך הבית.

    private FirebaseAuth mAuth;
    // רכיב האימות של Firebase.

    private FirebaseFirestore db;
    // רכיב בסיס הנתונים של Firebase.

    private TextView tvWelcome;
    // רכיב טקסט ב-UI להצגת הודעת ברוך הבא אישית (למשל: "שלום נבו!").

    private Button btnAuth;
    // כפתור דינמי שמשנה את פניו: מציג "כניסה/הרשמה" אם המשתמש מנותק, ו-"התנתק" אם המשתמש מחובר.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // פונקציית היצירה של המסך שרצה ראשונה.

        super.onCreate(savedInstanceState);
        // ביצוע אתחול בסיסי של מחלקת האב באנדרואיד.

        setContentView(R.layout.activity_home);
        // חיבור קובץ ה-XML העיצובי של מסך הבית למחלקת הג'אווה הנוכחית.

        mAuth = FirebaseAuth.getInstance();
        // קבלת מופע פעיל של מערכת האימות מהענן.

        db = FirebaseFirestore.getInstance();
        // קבלת מופע פעיל של בסיס הנתונים מהענן.

        tvWelcome = findViewById(R.id.tv_welcome_user);
        // קישור משתנה הטקסט לרכיב ה-XML המתאים שמציג את הודעת הברוך הבא.

        btnAuth = findViewById(R.id.btn_auth);
        // קישור משתנה כפתור האימות לרכיב ה-XML שלו.

        Button btnStart = findViewById(R.id.btn_start_game);
        // קישור כפתור התחלת משחק חדש.

        Button btnLeaderboard = findViewById(R.id.btn_leaderboard);
        // קישור כפתור המעבר ללוח התוצאות והיסטוריית המשחקים.

        btnLeaderboard.setOnClickListener(v -> {
            // הגדרת מאזין לחיצה לכפתור לוח התוצאות.

            if (mAuth.getCurrentUser() == null) {
                // בדיקה הגנתית: אם אין אף משתמש מחובר כרגע למכשיר (התוצאה היא null):

                showLoginRequiredDialog();
                // לא נאפשר לו להיכנס ללוח התוצאות, אלא נקפיץ דיאלוג שמסביר שעליו להתחבר קודם.
            } else {
                // אם המשתמש מחובר:

                Intent intent = new Intent(HomeActivity.this, LeaderboardActivity.class);
                // נפתח כוונה (Intent) לעבור למסך לוח התוצאות (LeaderboardActivity).

                startActivity(intent);
                // נבצע את המעבר למסך בפועל.
            }
        });

        btnStart.setOnClickListener(v -> showGameModeDialog());
        // הגדרת מאזין לכפתור התחלת משחק: בלחיצה, נקרא לפונקציה שמקפיצה דיאלוג לבחירת סוג המשחק.

        btnAuth.setOnClickListener(v -> {
            // הגדרת מאזין לחיצה לכפתור האימות הדינמי (כניסה / התנתקות):

            if (mAuth.getCurrentUser() != null) {
                // אם המשתמש מחובר כרגע, המשמעות של הלחיצה היא שהוא מעוניין *להתנתק*:

                mAuth.signOut();
                // פקודה של Firebase המנתקת את המשתמש הנוכחי מהמערכת מיידית.

                checkUserStatus();
                // קריאה לפונקציית בדיקת המצב כדי לעדכן מיד את הנראות של ה-UI (לשנות את הכפתור חזרה ל"כניסה").

                Toast.makeText(this, "התנתקת בהצלחה", Toast.LENGTH_SHORT).show();
                // הצגת טקסט קופץ קצר המאשר את הניתוק.
            } else {
                // אם המשתמש מנותק כרגע, המשמעות של הלחיצה היא שהוא מעוניין *להתחבר*:

                startActivity(new Intent(this, LoginActivity.class));
                // מעבר מיידי למסך ההתחברות (LoginActivity).
            }
        });

        checkUserStatus();
        // הרצת בדיקת מצב המשתמש כבר ברגע שהמסך נפתח לראשונה, כדי לעצב את המסך נכון למצב שלו.
    }

    private void showGameModeDialog() {
        // פונקציה המציגה תפריט בחירה קופץ (AlertDialog) עם מצבי המשחק השונים של השש-בש.

        String[] options = {"צור משחק", "הצטרף למשחק", "משחק יחיד (מקומי)"};
        // מערך של מחרוזות טקסט המייצגות את האפשרויות שיופיעו בתפריט.

        new AlertDialog.Builder(this)
                // יצירת בנאי של תיבת דיאלוג קופצת במסך הנוכחי.

                .setTitle("בחר מצב משחק")
                // הגדרת הכותרת הראשית של תיבת הדיאלוג.

                .setItems(options, (dialog, which) -> {
                    // הזרקת מערך האפשרויות לתוך הדיאלוג, והגדרת מאזין לחיצה שמקבל פרמטר בשם 'which' (מספר האינדקס של האפשרות שנבחרה, החל מ-0).

                    if (which == 2) {
                        // אינדקס 2 מייצג את האפשרות השלישית: "משחק יחיד (מקומי)". משחק זה תמיד מותר, גם ללא חיבור לאינטרנט.

                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        // יצירת כוונה למעבר למסך המשחק הראשי (MainActivity).

                        intent.putExtra("mode", "local");
                        // הזרקת מידע נוסף לתוך ה-Intent (זה נקרא Extra): מפתח בשם "mode" עם הערך "local", כדי שמסך המשחק ידע להפעיל מצב לא מקוון.

                        startActivity(intent);
                        // מעבר למסך המשחק.
                    } else {
                        // אינדקס 0 ("צור משחק") או אינדקס 1 ("הצטרף למשחק") – שניהם משחקים ברשת ודורשים אינטרנט ומשתמש מחובר.

                        if (mAuth.getCurrentUser() == null) {
                            // אם המשתמש מנסה לשחק ברשת אבל הוא מנותק מהמערכת:

                            showLoginRequiredDialog();
                            // נציג לו את דיאלוג השגיאה שדורש התחברות קודם לכן.
                        } else {
                            // אם הוא מחובר ומאושר למשחק רשת:

                            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                            // יצירת כוונה למעבר למסך המשחק הראשי.

                            intent.putExtra("mode", which == 0 ? "create" : "join");
                            // שימוש באופרטור תנאי מקוצר (Ternary Operator): אם שנבחר אינדקס 0 נשלח במצב "create", אחרת (נבחר 1) נשלח במצב "join".

                            startActivity(intent);
                            // מעבר למסך המשחק הראשי ברשת.
                        }
                    }
                })
                .show();
        // פקודה חובה בסוף השרשור שמציגה בפועל את תיבת הדיאלוג על מסך המכשיר.
    }

    private void showLoginRequiredDialog() {
        // פונקציה המקפיצה דיאלוג אזהרה כאשר משתמש מנסה לבצע פעולה שדורשת התחברות (כמו לוח תוצאות או משחק רשת).

        new AlertDialog.Builder(this)
                .setTitle("נדרשת התחברות")
                // כותרת הדיאלוג.

                .setMessage("עליך להתחבר למערכת כדי להשתמש באפשרות זו.")
                // תוכן הודעת ההסבר בגוף הדיאלוג.

                .setPositiveButton("להתחברות", (dialog, which) -> {
                    // הגדרת כפתור אישור חיובי ("להתחברות") ומאזין שלחיצה עליו מעבירה את המשתמש ישירות למסך ההתחברות.

                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                })
                .setNegativeButton("ביטול", null)
                // הגדרת כפתור שלילי ("ביטול"). הערך null אומר שלחיצה עליו פשוט תסגור את הדיאלוג מבלי לעשות כלום.

                .show();
        // הצגת הדיאלוג על המסך.
    }

    private void checkUserStatus() {
        // פונקציה קריטית הבודקת האם יש משתמש מחובר ומעדכנת את כל רכיבי הויזואליות במסך בהתאם למצב הנוכחי שלו.

        FirebaseUser user = mAuth.getCurrentUser();
        // שליפת המשתמש המחובר כרגע מה-Auth. המשתנה יכיל אובייקט משתמש, או null אם אף אחד לא מחובר.

        if (user != null) {
            // מקרה א': יש משתמש מחובר למכשיר!

            btnAuth.setText("התנתק");
            // שינוי כיתוב הכפתור הראשי ל-"התנתק".

            db.collection("users").document(user.getUid()).get()
                    // פנייה ל-Firestore לאוסף המשתמשים, שליפת המסמך הספציפי ששמו הוא ה-UID הייחודי של השחקן המחובר, וביצוע קריאה (get).

                    .addOnSuccessListener(doc -> {
                        // רישום מאזין שירוץ רק במידה והשליפה מהענן הצליחה.

                        if (doc.exists()) {
                            // בדיקה שאכן קיים מסמך כזה בפועל בבסיס הנתונים:

                            String name = doc.getString("username");
                            // שליפת המחרוזת שנמצאת תחת השדה "username" בתוך המסמך של המשתמש.

                            tvWelcome.setText("שלום " + name + "!");
                            // עדכון רכיב הטקסט הגרפי עם הודעת ברוך הבא אישית הכוללת את שמו.

                            tvWelcome.setVisibility(View.VISIBLE);
                            // הגדרת רכיב הטקסט כ"גלוי" (VISIBLE) על גבי המסך.
                        }
                    });
        } else {
            // מקרה ב': אין משתמש מחובר (user הוא null), כלומר המשתמש אורח/מנותק.

            btnAuth.setText("כניסה/הרשמה");
            // שינוי כיתוב הכפתור הראשי ל-"כניסה/הרשמה".

            tvWelcome.setVisibility(View.GONE);
            // העלמת רכיב טקסט הברוך הבא לחלוטין מהמסך (GONE) כדי שלא יתפוס מקום בעיצוב.
        }
    }

    @Override
    protected void onResume() {
        // פונקציית מחזור חיים של אנדרואיד. היא רצה אוטומטית בכל פעם שהמשתמש *חוזר* למסך הבית (למשל, אחרי שהיה במסך התחברות וסגר אותו).

        super.onResume();
        // קריאה לפעולות הבסיס של מחלקת האב.

        checkUserStatus();
        // הרצה מחודשת של בדיקת הסטטוס, כדי שאם המשתמש הרגע התחבר במסך הקודם וחזר, מסך הבית יתעדכן מיד בשמו החדש ללא השהייה.
    }
}