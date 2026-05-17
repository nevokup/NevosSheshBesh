package com.example.nevos_shesh_besh;
// מגדיר את החבילה (Package) שבה נמצא הקובץ, כדי שאנדרואיד ידע לשייך אותו לפרויקט.

public class GameRecord {
// הגדרת המחלקה הציבורית בשם GameRecord.

    private String opponentName;
    // משתנה טקסט פרטי השומר את שם השחקן היריב ששיחקנו נגדו.

    private String winner;    // "אני" או שם היריב
    // משתנה טקסט פרטי השומר את זהות המנצח (יכיל את המחרוזת "אני" או את שם היריב).

    private String winType;   // "רגיל", "מרס", "מרס כוכבים"
    // משתנה טקסט פרטי השומר את סוג הניצחון שהושג במשחק.

    private long timestamp;   // למיון לפי זמן
    // משתנה מסוג מספר ארוך (long) השומר את הזמן המדויק שבו המשחק הסתיים, כדי שנוכל למיין את המשחקים בלוח התוצאות מהחדש לישן.

    private String playerUid;
    // משתנה טקסט פרטי השומר את מזהה המשתמש הייחודי (UID) של השחקן בענן, כדי לדעת למי שייכת ההיסטוריה הזו.

    public GameRecord() {} // חובה עבור Firestore
    // בנאי (Constructor) ריק ללא פרמטרים. טיפ למבחן: ה-SDK של Firestore חייב בנאי ריק כדי להפוך את המידע הגולמי מהענן לאובייקט Java באופן אוטומטי.

    public GameRecord(String opponentName, String winner, String winType, long timestamp, String playerUid) {
        // בנאי עם פרמטרים, המשמש ליצירת אובייקט חדש של רשומת משחק כאשר אנחנו רוצים לשמור משחק שזה עתה הסתיים.

        this.opponentName = opponentName;
        // לוקח את השם שקיבלנו בפרמטר ומציב אותו במשתנה הפרטי של המחלקה.

        this.winner = winner;
        // לוקח את המנצח שקיבלנו בפרמטר ומציב אותו במשתנה הפרטי של המחלקה.

        this.winType = winType;
        // לוקח את סוג הניצחון שקיבלנו ומציב אותו במשתנה הפרטי.

        this.timestamp = timestamp;
        // לוקח את ערך הזמן שקיבלנו ומציב אותו במשתנה הפרטי.

        this.playerUid = playerUid;
        // לוקח את מזהה המשתמש שקיבלנו ומציב אותו במשתנה הפרטי.
    }

    // פונקציות גטרים (Getters) – מאפשרות למחלקות חיצוניות לקרוא את המשתנים הפרטיים (private) של המחלקה הזו:
    public String getOpponentName() { return opponentName; }
    // מחזירה את שם היריב.

    public String getWinner() { return winner; }
    // מחזירה את זהות המנצח.

    public String getWinType() { return winType; }
    // מחזירה את סוג הניצחון.

    public long getTimestamp() { return timestamp; }
    // מחזירה את חותמת הזמן של המשחק.

    public String getPlayerUid() { return playerUid; }
    // מחזירה את מזהה השחקן.
}