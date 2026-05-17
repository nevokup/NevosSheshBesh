package com.example.nevos_shesh_besh;
// שיוך החבילה של האפליקציה.

import com.example.nevos_shesh_besh.model.Game;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Map;
// ייבוא מודל המשחק ומחלקות השירות של Firestore.

public class NetworkManager {
// הגדרת מחלקת מנהל הרשת.

    private FirebaseFirestore db;
    // רכיב הגישה הראשי לבסיס הנתונים בענן.

    private DocumentReference gameRef;
    // משתנה מסוג "הפניה למסמך" (DocumentReference). הוא שומר בזיכרון את הכתובת המדויקת של מסמך המשחק הספציפי בענן שבו השחקנים משחקים כרגע.

    public interface OnGameUpdateListener {
        // הגדרת ממשק (Interface) פנימי בשם OnGameUpdateListener. טיפ למבחן: זהו מנגנון Callback (קריאה חוזרת). הוא מאפשר למחלקות אחרות (כמו מסך המשחק הראשי) "להקשיב" למחלקה הזו, ומתי שהרשת תתעדכן, הפונקציה onUpdate תופעל ותעביר אליהם את המידע החדש.

        void onUpdate(Map<String, Object> data);
        // הגדרת חתימת פונקציה המקבלת מפה (Map) של מפתח וערך המייצגת את נתוני המשחק המעודכנים שהגיעו מהענן.
    }

    public NetworkManager() {
        // בנאי של מנהל הרשת.

        try { db = FirebaseFirestore.getInstance(); } catch (Exception e) {}
        // ניסיון לקבל מופע פעיל של בסיס הנתונים מהענן בתוך בלוק try-catch כדי למנוע קריסה במידה ורכיבי ה-SDK של Firebase עדיין לא סונכרנו או אותחלו במלואם בפרויקט.
    }

    public void createGame(String code, Game game, OnGameUpdateListener listener) {
        // פונקציה ליצירת משחק רשת חדש בענן. מקבלת קוד משחק ייחודי, את אובייקט לוגיקת המשחק (game), ואת המאזין לעדכונים.

        gameRef = db.collection("Games").document(code);
        // הגדרת כתובת המסמך בענן: בתוך אוסף שנקרא "Games", ניצור מסמך שהשם שלו יהיה קוד המשחק שהוזן (למשל "1234").

        gameRef.set(game.toMap());
        // פקודת .set() השומרת ומעלה את נתוני המשחק לענן בפעם הראשונה. הפונקציה game.toMap() הופכת את אובייקט המשחק למפה של ערכים ש-Firestore יודע לקרוא.

        listenToUpdates(listener);
        // הפעלת פונקציית ההאזנה כדי להתחיל להקשיב לשינויים שהיריב יבצע במשחק הזה החל מרגע זה.
    }

    public void joinGame(String code, OnGameUpdateListener listener) {
        // פונקציה עבור שחקן שמצטרף למשחק רשת קיים שכבר נוצר בענן על ידי שחקן אחר, באמצעות קוד המשחק.

        gameRef = db.collection("Games").document(code);
        // קישור והפניה למסמך הקיים בענן לפי קוד המשחק שהוקלד.

        listenToUpdates(listener);
        // הפעלת האזנה מיידית לשינויים במסמך זה כדי שהשחקן המצטרף יסתנכרן עם הלוח של יוצר המשחק.
    }

    public void updateGameState(Game game) {
        // פונקציה שנקראת מתוך מסך המשחק בכל פעם שהשחקן הנוכחי עושה מהלך חוקי על גבי הלוח.

        if (gameRef != null) gameRef.update(game.toMap());
        // בדיקה שיש לנו הפניה פעילה למשחק, ואז שליחת פקודת .update() לענן המעדכנת רק את השדות שהשתנו בלוח, כדי שהשחקן השני יראה את המהלך בצד שלו באופן מיידי.
    }

    private void listenToUpdates(OnGameUpdateListener listener) {
        // פונקציה פנימית ופרטית המנהלת את ערוץ ההקשבה הפתוח מול שרתי Firestore בזמן אמת.

        gameRef.addSnapshotListener((snapshot, e) -> {
            // שימוש בפקודה addSnapshotListener. היא משאירה "צינור פתוח" לענן. בכל פעם ששחקן כלשהו (אתה או היריב) ישנה את המידע במסמך המשחק בענן, הפונקציה הזו תתעורר מיידית ותקבל תצלום מצב (snapshot) עדכני של המידע.

            if (snapshot != null && snapshot.exists()) listener.onUpdate(snapshot.getData());
            // בדיקה שהתצלום שחזר אינו ריק ושהמסמך אכן קיים בענן, ואז הפעלת פונקציית ה-Callback של המאזין (onUpdate) ושליחת נתוני המפה העדכניים (`snapshot.getData()`) אל מסך המשחק הראשי לצורך ציור מחדש של הלוח.
        });
    }
}