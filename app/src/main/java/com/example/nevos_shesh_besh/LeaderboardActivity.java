package com.example.nevos_shesh_besh;
// שיוך החבילה של האפליקציה.

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
// ייבוא רכיבי רשימה מתקדמים (RecyclerView) ורכיבי השאילתות של Firestore.

public class LeaderboardActivity extends AppCompatActivity {
// הגדרת מחלקת מסך לוח התוצאות וההיסטוריה.

    private TextView tvPlayerName, tvTotal, tvTotalWins, tvWinRegular, tvWinMars, tvWinStars, tvLosses;
    // הגדרת משתנים עבור כל רכיבי הטקסט (TextView) המופיעים בראש המסך ומציגים את סיכומי הנתונים הסטטיסטיים.

    private GameAdapter adapter;
    // הגדרת משתנה עבור המתאם (Adapter) – המחלקה שמתווכת ומזריקה את רשימת המשחקים לתוך ה-RecyclerView הויזואלי.

    private List<GameRecord> gameList = new ArrayList<>();
    // יצירת רשימה דינמית (ArrayList) ריקה שתשמור בזיכרון את אובייקטי ה-GameRecord שישלפו מהענן.

    private FirebaseFirestore db;
    // רכיב בסיס הנתונים של Firebase לשליפת נתוני המשחקים.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // פונקציית היצירה הראשונית של המסך.

        super.onCreate(savedInstanceState);
        // קריאה לפונקציית האב באנדרואיד.

        setContentView(R.layout.activity_leaderboard);
        // חיבור קובץ ה-XML העיצובי של לוח התוצאות למחלקה הנוכחית.

        db = FirebaseFirestore.getInstance();
        // קבלת מופע פעיל של בסיס הנתונים מהענן.

        // קישור פיזי של כל משתני הטקסט שהגדרנו למעלה אל רכיבי ה-TextView המתאימים בקובץ ה-XML באמצעות ה-ID הייחודי שלהם:
        tvPlayerName = findViewById(R.id.tv_player_name_summary);
        tvTotal = findViewById(R.id.tv_total_games);
        tvTotalWins = findViewById(R.id.tv_total_wins_count);
        tvWinRegular = findViewById(R.id.tv_win_type_regular);
        tvWinMars = findViewById(R.id.tv_win_type_mars);
        tvWinStars = findViewById(R.id.tv_win_type_stars);
        tvLosses = findViewById(R.id.tv_losses_count);

        Button btnBack = findViewById(R.id.btn_back);
        // מציאת כפתור החזרה.

        btnBack.setOnClickListener(v -> finish());
        // הגדרת מאזין לחיצה לכפתור חזרה שסוגר את המסך ומחזיר אותנו למסך הבית.

        RecyclerView rv = findViewById(R.id.rv_history);
        // מציאת רכיב ה-RecyclerView מה-XML שאחראי להציג רשימה נגללת ויעילה של פריטים.

        rv.setLayoutManager(new LinearLayoutManager(this));
        // הגדרת מנהל פריסה (LayoutManager) מסוג לינארי (LinearLayoutManager). תפקידו לסדר את שורות הרשימה אחת מתחת לשנייה בצורה אנכית רגילה.

        adapter = new GameAdapter(gameList);
        // יצירת מופע חדש של המתאם (GameAdapter) ושליחת רשימת המשחקים הריקה (gameList) אליו כפרמטר בבנאי.

        rv.setAdapter(adapter);
        // חיבור רשמי של המתאם אל רכיב ה-RecyclerView במסך, כך שהרכיב יודע ממי לקבל את הפריטים לציור.

        loadUserData();
        // קריאה לפונקציות פנימיות שטוענות את שם המשתמש מהענן.

        loadGamesData();
        // קריאה לפונקציה ששולפת את היסטוריית המשחקים ומחשבת את הסטטיסטיקות.
    }

    private void loadUserData() {
        // פונקציה הטוענת את שם השחקן המחובר ומציגה אותו בראש מסך הסטטיסטיקה.

        String uid = FirebaseAuth.getInstance().getUid();
        // שליפת ה-UID (המזהה הייחודי) של המשתמש המחובר כרגע.

        if (uid == null) return;
        // בדיקת הגנה: אם מסיבה כלשהי אף אחד לא מחובר, נעצור את הפונקציה מיידית (return) כדי למנוע קריסה.

        db.collection("users").document(uid).get()
                // פנייה לאוסף "users", כניסה למסמך הספציפי של השחקן לפי ה-UID שלו וביצוע קריאה מהשרת.

                .addOnSuccessListener(documentSnapshot -> {
                    // מאזין שממתין לסיום השליפה בהצלחה.

                    if (documentSnapshot.exists()) {
                        // אם המסמך נמצא בענן:

                        String name = documentSnapshot.getString("username");
                        // שליפת הערך של השדה "username" מהמסמך.

                        if (name != null) {
                            tvPlayerName.setText(name);
                            // הצגת שם המשתמש האמיתי בתוך ה-TextView המיועד לכך בראש המסך.
                        }
                    }
                });
    }

    private void loadGamesData() {
        // פונקציה השולפת את כל היסטוריית המשחקים של השחקן, סופרת את סוגי הניצחונות ומעדכנת את הרשימה הויזואלית.

        String uid = FirebaseAuth.getInstance().getUid();
        // שליפת ה-UID של השחקן הנוכחי.

        if (uid == null) return;
        // הגנה: עצירת הפונקציה אם המשתמש ריק.

        db.collection("games")
                // פנייה לאוסף הכללי שנקרא "games" בענן (שם נשמרים כל המשחקים של כל המשתמשים באפליקציה).

                .whereEqualTo("playerUid", uid)
                // סינון (Filtering): "תביא לי רק משחקים שבהם השדה playerUid שווה בדיוק ל-UID של המשתמש המחובר כרגע".

                .orderBy("timestamp", Query.Direction.DESCENDING)
                // מיון (Sorting): מיון כל תוצאות המשחקים לפי שדה הזמן שלהם (timestamp) בסדר יורד (DESCENDING), כלומר מהמשחק הכי חדש שקרה ועד למשחק הכי ישן.

                .addSnapshotListener((value, error) -> {
                    // שימוש במאזין מסוג SnapshotListener. טיפ למבחן: מאזין זה פותח ערוץ קבוע (Real-time). אם נשחק משחק נוסף והוא יישמר בענן, הפונקציה הזו תתעורר ותרוץ שוב אוטומטית, והמסך יתעדכן מיידית בלי צורך לצאת ולהיכנס מחדש!

                    if (error != null) return;
                    // אם אירעה שגיאה כלשהי בתקשורת מול השרת (למשל בעיית הרשאות), נעצור את הריצה.

                    if (value == null) return;
                    // אם המידע שחזר מהענן הוא ריק לחלוטין (null), נעצור.

                    gameList.clear();
                    // ניקוי מוחלט של הרשימה הישנה בזיכרון (gameList) לפני שמכניסים את המידע העדכני, כדי למנוע כפילויות של שורות במסך.

                    int total = 0, reg = 0, mars = 0, stars = 0;
                    // הגדרת משתני מונים (Counters) זמניים מסוג מספר שלם (int) ומאופסים, שישמשו אותנו לספירת הנתונים: סך הכל משחקים, ניצחונות רגילים, ניצחונות מרס, ומרס כוכבים.

                    for (QueryDocumentSnapshot doc : value) {
                        // לולאת For-Each הרצה ועוברת אחד-אחד על כל מסמכי המשחקים (Documents) שחזרו מהשאילתה בענן.

                        GameRecord g = doc.toObject(GameRecord.class);
                        // פקודת קסם של Firebase: הפיכת המידע הגולמי (JSON) שנמצא בתוך ה-doc למחלקה מסוג GameRecord מוכנה בג'אווה, בזכות הבנאי הריק שהגדרנו בה.

                        gameList.add(g);
                        // הוספת אובייקט המשחק החדש שנוצר אל תוך הרשימה שלנו (gameList) שמזינה את המסך.

                        total++;
                        // קידום מונה סך כל המשחקים ב-1 בכל פעם שמצאנו רשומת משחק כלשהי.

                        if ("אני".equals(g.getWinner())) {
                            // בדיקה לוגית: אם המחרוזת של שדה המנצח (winner) שווה למילה "אני", זה אומר שהמשתמש המחובר הוא זה שניצח במשחק הספציפי הזה.

                            String type = g.getWinType();
                            // שליפת סוג הניצחון של המשחק הזה ("רגיל", "מרס", או "מרס כוכבים").

                            // מבנה תנאים מקונן המקדם את המונה הספציפי לפי סוג הניצחון שנמצא:
                            if ("רגיל".equals(type)) reg++;
                                // אם סוג הניצחון הוא רגיל, נקדם את מונה הרגיל ב-1.

                            else if ("מרס".equals(type)) mars++;
                                // אם סוג הניצחון הוא מרס, נקדם את מונה המרס ב-1.

                            else if ("מרס כוכבים".equals(type)) stars++;
                            // אם סוג הניצחון הוא מרס כוכבים, נקדם את מונה המרס כוכבים ב-1.
                        }
                    }

                    int winsCount = reg + mars + stars;
                    // חישוב מתמטי פשוט: סך כל הניצחונות של השחקן הוא חיבור של כל סוגי הניצחונות יחד.

                    // עדכון טקסטואלי של כל רכיבי ה-TextView במסך עם המספרים שחישבנו זה עתה:
                    tvTotal.setText("משחקים: " + total);
                    // הצגת סך הכל המשחקים ששוחקו.

                    tvTotalWins.setText(String.valueOf(winsCount));
                    // הצגת מספר הניצחונות הכולל (המרת המספר למחרוזת באמצעות String.valueOf).

                    tvWinRegular.setText("• ניצחון רגיל: " + reg);
                    // הצגת מספר הניצחונות הרגילים.

                    tvWinMars.setText("• ניצחון מארס: " + mars);
                    // הצגת מספר ניצחונות המרס.

                    tvWinStars.setText("• ניצחון מארס כוכבים: " + stars);
                    // הצגת מספר ניצחונות המרס כוכבים.

                    tvLosses.setText("הפסדים: " + (total - winsCount));
                    // חישוב והצגת מספר ההפסדים: סך כל המשחקים פחות סך כל הניצחונות שווה לכמות ההפסדים.

                    adapter.notifyDataSetChanged();
                    // פקודה קריטית למתאם (Adapter)! אנחנו מודיעים לו שהרשימה שבתוכו (gameList) השתנתה לחלוטין. בעקבות הפקודה הזו, ה-RecyclerView מצייר מחדש את השורות על המסך עם הנתונים החדשים מהענן.
                });
    }
}