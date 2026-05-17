package com.example.nevos_shesh_besh.UI;
// שיוך לחבילת ממשק המשתמש (UI).

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.List;
import com.example.nevos_shesh_besh.model.Game;
import com.example.nevos_shesh_besh.shapes.DieShape;
import com.example.nevos_shesh_besh.shapes.MiddleLineShape;
import com.example.nevos_shesh_besh.shapes.TriangleShape;
// ייבוא מחלקות הציור, אובייקטי הצורות שיצרנו, ומודל לוגיקת המשחק הכללי (Game).

public class CustomSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
// הגדרת מחלקת המשטח, היורשת מ-SurfaceView (משטח ציור מהיר) ומממשת את ה-Callback של SurfaceHolder כדי לדעת מתי המשטח נוצר, משתנה או נהרס במסך.

    private GameThread gameThread;
    // תהליך רקע עצמאי (Thread) שבו תתבצע לולאת הציור האינסופית של המשחק, כדי לא לתקוע את ה-Main Thread (התהליך הראשי של אנדרואיד).

    private final List<TriangleShape> triangles = new ArrayList<>();
    // רשימה דינמית המחזיקה את 24 אובייקטי המשולשים הגרפיים המרכיבים את לוח השש-בש.

    private MiddleLineShape middleLine;
    // אובייקט של קו האמצע (הבר).

    private DieShape die1;
    private DieShape die2;
    // שני אובייקטים גרפיים המייצגים את שתי קוביות המשחק שמצוירות על המסך.

    int screenWidth;
    int screenHeight;
    // משתנים לשמירת הרוחב והגובה הפיזיים של מסך המכשיר (בפיקסלים), כפי שיתקבלו ממערכת ההפעלה בזמן אמת.

    private int numberOfTriangles = 12;
    // משתנה המגדיר כמה משולשים יש בכל חצי אורך של לוח (12 בחלק התחתון ו-12 בחלק העליון, סך הכל 24).

    private Game game;
    // הפניה (Reference) לאובייקט לוגיקת המשחק הראשי (Game) המכיל את מצב הלוח האמיתי, התורות והקוביות.

    public CustomSurfaceView(Context context, Game game) {
        // בנאי של משטח הציור, המקבל את ה-Context (הקשר המסך) ואת אובייקט לוגיקת המשחק.

        super(context);
        // קריאה לבנאי של מחלקת האב SurfaceView.

        getHolder().addCallback(this);
        // רישום של המחלקה הנוכחית (this) כמאזינה למחזור החיים של המשטח, כדי שהפונקציות surfaceCreated וכו' יופעלו אוטומטית.

        this.game = game;
        // שמירת אובייקט המשחק שהתקבל במשתנה המחלקה.
    }

    private void initShapes() {
        // פונקציה פנימית המאתחלת ומחשבת את הגדלים והמיקומים הפיזיים של כל הצורות על הלוח בהתאם לרזולוציית המסך הנוכחית.

        initBoardTriangles();
        // קריאה לפונקציית אתחול 24 המשולשים הויזואליים של הלוח.

        middleLine = new MiddleLineShape(screenWidth, screenHeight, numberOfTriangles);
        // יצירת קו האמצע (הבר) ושליחת ממדי המסך אליו לצורך חישוב מיקומו המרכזי.

        RectF middleRect = middleLine.getRect();
        // שליפת מלבן הגבולות של קו האמצע כדי למקם את קוביות המשחק בדיוק בתוכו או לצידו.

        float dieSize = middleRect.width() * 0.8f;
        // קביעת גודל (אורך צלע) הקובייה כ-80% מרוחב קו האמצע, כדי שהקוביות ייכנסו יפה בתוך גבולות הבר.

        float dieX = middleRect.centerX() - (dieSize / 2);
        // חישוב מיקום ה-X של הקוביות: מרכז ה-X של הבר פחות חצי מגודל הקובייה (כך הקוביות ממורכזות במדויק בתוך הבר).

        float die1Y = screenHeight / 5f - dieSize / 2f;
        // חישוב מיקום ה-Y של קובייה 1: ממוקמת בחלק העליון (חמישית מגובה המסך).

        float die2Y = screenHeight * 4 / 5f - dieSize / 2f;
        // חישוב מיקום ה-Y של קובייה 2: ממוקמת בחלק התחתון (ארבע חמישיות מגובה המסך).

        die1 = new DieShape(game.dice[0], dieX, die1Y, dieSize);
        // יצירת האובייקט הגרפי של קובייה 1 עם הערך הנוכחי שלה מתוך מערך הקוביות של מודל המשחק (`game.dice[0]`).

        die2 = new DieShape(game.dice[1], dieX, die2Y, dieSize);
        // יצירת האובייקט הגרפי של קובייה 2.
    }

    private void initBoardTriangles() {
        // פונקציה מורכבת המחשבת את המיקום המתמטי המדויק על המסך עבור כל אחד מ-24 המשולשים של לוח השש-בש.

        triangles.clear();
        // ניקוי הרשימה לפני חישוב מחדש.

        float sectionWidth = (float) screenWidth / (numberOfTriangles + 1);
        // חלוקת רוחב המסך ל-13 מקטעים (12 משולשים + מקטע אחד פנוי באמצע עבור הבר).

        float drawWidth = (float) 0.9 * sectionWidth;
        // רוחב המשולש הפיזי שיצויר יהיה 90% מרוחב המקטע (כדי להשאיר רווח קטנטן ואסתטי בין משולש למשולש).

        float drawHeight = (float) 0.45 * screenHeight;
        // גובה המשולשים הפיזי נקבע כ-45% מגובה המסך הכולל (כך שהמשולשים התחתונים והעליונים כמעט נפגשים באמצע, ומשאירים 10% רווח ריק ביניהם).

        int colorDarkWood = Color.rgb(110, 50, 15);
        int colorLightWood = Color.rgb(186, 92, 28);
        // הגדרת שני צבעי עץ מתחלפים עבור המשולשים: חום עץ כהה וחום עץ בהיר.

        // --- חלק תחתון של הלוח (משולשים באינדקסים 0 עד 11) ---
        float drawY = screenHeight - 10;
        // בסיס המשולשים התחתונים מתחיל מתחתית המסך (גובה המסך פחות 10 פיקסלים של היסט קל).

        for (int i = 0; i < numberOfTriangles; i++) {
            // לולאה הרצה 12 פעמים לייצור 12 המשולשים התחתונים:

            float drawX = (i * sectionWidth) + (sectionWidth / 2);
            // חישוב מיקום ה-X של מרכז המשולש הנוכחי בלולאה.

            if (i >= numberOfTriangles / 2) drawX += sectionWidth;
            // טיפ חשוב למבחן: אם הגענו לחצי הלוח (אינדקס 6 ומעלה), אנחנו מוסיפים ל-X עוד `sectionWidth` שלם. בכך אנחנו "מדלגים" בכוונה על מקטע אחד באמצע הלוח ומפנים מקום לקו האמצע (הבר)!

            int color = (i % 2 == 0) ? colorDarkWood : colorLightWood;
            // בחירת צבע מתחלף לסירוגין באמצעות בדיקת שארית חלוקה ב-2 (מודולו): אינדקס זוגי יקבל צבע כהה, אינדקס אי-זוגי יקבל צבע בהיר.

            triangles.add(new TriangleShape(drawX, drawY, drawWidth, drawHeight, false, color, game.board[i]));
            // יצירת אובייקט המשולש והוספתו לרשימה. הפרמטר false אומר שהמשולש פונה *כלפי מעלה* (בסיס בתחתית, קודקוד למעלה), ו`game.board[i]` שולח את מערך האבנים האמיתי שנמצא במשולש זה מהמודל.
        }

        // --- חלק עליון של הלוח (משולשים באינדקסים 12 עד 23) ---
        drawY = 0;
        // בסיס המשולשים העליונים מתחיל ממש בקצה העליון של המסך (Y=0).

        for (int i = 0; i < numberOfTriangles; i++) {
            // לולאה הרצה 12 פעמים לייצור 12 המשולשים העליונים:

            float drawX = screenWidth - ((i * sectionWidth) + (sectionWidth / 2));
            // בשש-בש, המשולשים העליונים ממוספרים בכיוון ההפוך (מצד ימין של המסך ונעים שמאלה), לכן החישוב מתחיל מ-screenWidth פחות המיקום היחסי.

            if (i >= numberOfTriangles / 2) drawX -= sectionWidth;
            // אם הגענו לחצי הלוח העליון, נחסיר `sectionWidth` כדי לדלג שמאלה ולהשאיר את מרווח הבר במרכז הלוח.

            int color = (i % 2 == 0) ? colorLightWood : colorDarkWood;
            // בחירת צבע מתחלף לסירוגין (בסדר הפוך מהחלק התחתון ליצירת מראה לוח שש-בש מסורתי).

            triangles.add(new TriangleShape(drawX, drawY, drawWidth, drawHeight, true, color, game.board[i + numberOfTriangles]));
            // יצירת אובייקט המשולש העליון והוספתו לרשימה. הפרמטר true קובע שהמשולש פונה *כלפי מטה* (בסיס למעלה, קודקוד למטה).
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // פונקציית Callback מובנית של אנדרואיד. היא מופעלת אוטומטית ברגע שמשטח הציור נוצר פיזית ומוכן לעבודה על גבי המסך.

        gameThread = new GameThread(getHolder(), this);
        // יצירת אובייקט ה-Thread (תהליך הרקע של הציור) ושליחת ה-Holder והמשטח הנוכחי אליו.

        gameThread.setRunning(true);
        // שינוי דגל הריצה של ה-Thread למצב true (פעיל).

        gameThread.start();
        // פקודה המזניקה ומפעילה את תהליך הרקע. החל מרגע זה, פונקציית run() של ה-Thread מתחילה לרוץ בלולאה מהירה מאחורי הקלעים.
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // פונקציית Callback המופעלת ברגע שחל שינוי במשטח (למשל, כשהוא מקבל גודל סופי לראשונה). היא מספקת לנו את רוחב (`width`) וגובה (`height`) המסך האמיתיים של המכשיר בפיקסלים.

        screenWidth = width;
        screenHeight = height;
        // שמירת הגדלים המדויקים שנתקבלו במשתני המחלקה.

        initShapes();
        // קריאה לאתחול וחישוב מיקומי כל הצורות, מכיוון שכעת יש לנו את מימדי המסך האמיתיים והמדויקים.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // פונקציית Callback המופעלת אוטומטית כאשר המשתמש סוגר את המסך או יוצא מהאפליקציה, ומשטח הציור מושמד.

        boolean retry = true;
        // משתנה בוליאני זמני לניהול לולאת סגירת ה-Thread.

        gameThread.setRunning(false);
        // שינוי דגל הריצה של ה-Thread ל-false, מה שגורם ללולאת הציור האינסופית שלו להיעצר מיידית בסיבוב הבא.

        while (retry) {
            // לולאה הממתינה עד שתהליך הרקע יסיים את פעולתו לחלוטין וימות בצורה מסודרת ובטוחה:

            try {
                gameThread.join();
                // פקודה שמקפיאה את הריצה הנוכחית וממתינה עד שה-gameThread יסיים לחלוטין את הריצה שלו ויסגר.

                retry = false;
                // אם ה-join הצליח וה-Thread מת, נשנה ל-false כדי לצאת מהלולאה.
            } catch (InterruptedException e) {}
            // תפיסת שגיאת קטיעה (אם קרתה) והמשך ניסיון סגירה בסיבוב הבא של הלולאה.
        }
    }

    @Override
    public void draw(Canvas canvas) {
        // פונקציית הציור הראשית. היא נקראת ללא הפסקה עשרות פעמים בשנייה מתוך ה-GameThread שעובד ברקע.

        super.draw(canvas);
        // קריאה לפונקציית הבסיס של אנדרואיד לניקוי ראשוני.

        if (canvas == null) return;
        // הגנה: אם מסיבה כלשהי קנבס הציור אינו זמין כרגע, נעצור מיד כדי למנוע קריסת מערכת.

        canvas.drawColor(Color.rgb(160, 100, 60));
        // צביעה מחדש של כל רקע הלוח בגוון חום עץ קלאסי (Sienna). פקודה זו למעשה "מוחקת" את הציור של הפריים הקודם ומכינה דף חלק לפריים החדש.

        for (int i = 0; i < triangles.size(); i++) {
            // לולאה העוברת על כל 24 המשולשים הגרפיים השמורים ברשימה:

            triangles.get(i).draw(canvas, game.board[i]);
            // פקודה לכל משולש לצייר את עצמו על הקנבס, ובנוסף לצייר מעליו את אבני המשחק האמיתיות שקיימות בו כרגע לפי מערך האבנים של מודל המשחק (`game.board[i]`).
        }

        if (middleLine != null) middleLine.draw(canvas, game.p1EatenCount, game.p2EatenCount);
        // אם קו האמצע אותחל, נבקש ממנו לצייר את עצמו ואת אבני השחקנים האכולות העדכניות שלו.

        if (die1 != null && die2 != null) {
            // אם אובייקטי הקוביות קיימים ומאותחלים:

            die1.setNumber(game.dice[0]);
            // נעדכן את אובייקט קובייה 1 עם הערך הדיגיטלי הנוכחי שלה מתוך מודל הלוגיקה (game.dice[0]).

            die2.setNumber(game.dice[1]);
            // נעדכן את אובייקט קובייה 2 עם הערך שלה (game.dice[1]).

            die1.draw(canvas);
            die2.draw(canvas);
            // נפקוד על שתי הקוביות הגרפיות לצייר את עצמן ואת נקודות המספרים שלהן על גבי הקנבס.
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // פונקציה מובנית באנדרואיד המופעלת אוטומטית בכל פעם שהמשתמש נוגע פיזית במסך עם האצבע. היא מספקת לנו אובייקט אירוע (event) המכיל את נתוני הנגיעה.

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // בדיקה: נטפל באירוע רק במידה וסוג הנגיעה הוא ACTION_DOWN (רגע ההורדה הראשוני של האצבע על המסך, התחלת הלחיצה).

            float x = event.getX();
            float y = event.getY();
            // שליפת קואורדינטות ה-X וה-Y המדויקות בפיקסלים של נקודת הנגיעה של האצבע על הלוח.

            for (int i = 0; i < triangles.size(); i++) {
                // לולאה הרצה ועוברת על כל 24 המשולשים הגרפיים בלוח אחד-אחד:

                if (triangles.get(i).isTouched(x, y)) {
                    // בדיקה: נשאל את המשולש הנוכחי בלולאה "האם נקודת הלחיצה (x,y) נמצאת בתוך הגבולות הפיזיים שלך?" (באמצעות פונקציית ה-isTouched שלו).

                    game.move(i);
                    // אם המשתמש אכן לחץ בתוך המשולש הזה, נפעיל מיידית את פונקציית המהלך של לוגיקת המשחק: `game.move(i)` ושולחים לה את אינדקס המשולש שנלחץ (0-23). מחלקת הלוגיקה כבר תחליט בעצמה לפי חוקי השש-בש אם מדובר בבחירת אבן להזזה או ביעד להנחת אבן.

                    return true;
                    // החזרת true ועצירת הלולאה – מצאנו את המשולש שנלחץ, אין צורך להמשיך לבדוק את שאר המשולשים בסיבוב זה.
                }
            }
        }
        return true;
        // החזרת true המאשרת למערכת ההפעלה שאירוע הנגיעה טופל בהצלחה על ידי הרכיב שלנו.
    }
}