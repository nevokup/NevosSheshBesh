package com.example.nevos_shesh_besh.shapes;
// שיוך לחבילת הצורות.

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
// ייבוא רכיבי הציור ואובייקט מלבן עשרוני (RectF).

public class MiddleLineShape {
// הגדרת מחלקת קו האמצע (הבר).

    private RectF rect;
    // אובייקט מובנה באנדרואיד המייצג קופסת מלבן גרפית בעלת ערכים עשרוניים (Left, Top, Right, Bottom) המשמשת כמבנה הפיזי של קו האמצע.

    private Paint paint;
    // מברשת צבע עבור ציור רקע קו האמצע מעץ.

    private Paint p1Paint;
    // מברשת צבע ייעודית עבור האבנים האכולות של שחקן 1 (בצבע לבן).

    private Paint p2Paint;
    // מברשת צבע ייעודית עבור האבנים האכולות של שחקן 2 (בצבע אפור/שחור כהה).

    private float screenWidth;
    // משתנה השומר את רוחב המסך הכולל של המכשיר, לצורך חישובי גדלים פרופורציונליים של האבנים.

    public MiddleLineShape(int screenWidth, int screenHeight, int numberOfTriangles) {
        // בנאי של קו האמצע המקבל את רוחב המסך, גובה המסך, וכמות המשולשים בחצי לוח (12).

        this.screenWidth = screenWidth;
        // שמירת רוחב המסך במשתנה המחלקה.

        float sectionWidth = (float) screenWidth / (numberOfTriangles + 1);
        // חישוב רוחב של סקציה (מקטע) אחת על הלוח: מחלקים את רוחב המסך ב-13 (12 משולשים ועוד מקטע אחד פנוי שמיועד עבור קו האמצע).

        float middleLineWidth = sectionWidth;
        // רוחב קו האמצע עצמו נקבע בדיוק בגודל של סקציה אחת שלמה.

        float left = (screenWidth / 2f) - (middleLineWidth / 2f);
        // חישוב הגבול השמאלי של קו האמצע: לוקחים את מרכז המסך המדויק ומזיזים שמאלה בחצי מרוחב הפס.

        float top = 0;
        // הגבול העליון של הפס מתחיל ממש בקצה העליון של המסך (0).

        float right = (screenWidth / 2f) + (middleLineWidth / 2f);
        // חישוב הגבול הימני: מרכז המסך פלוס חצי מרוחב הפס.

        float bottom = screenHeight;
        // הגבול התחתון נמתח לכל אורך גובה המסך (screenHeight).

        rect = new RectF(left, top, right, bottom);
        // יצירת אובייקט המלבן הפיזי החוסם את קו האמצע עם ארבעת הגבולות שחישבנו זה עתה.

        paint = new Paint();
        paint.setColor(Color.rgb(60, 30, 10));
        // אתחול המברשת של הפס והגדרת גוון חום עץ כהה מאוד (באמצעות קוד RGB).

        p1Paint = new Paint();
        p1Paint.setColor(Color.WHITE);
        // אתחול המברשת של השחקן הראשון וצביעתו בלבן מלא.

        p2Paint = new Paint();
        p2Paint.setColor(Color.rgb(40, 40, 40));
        // אתחול המברשת של השחקן השני וצביעתו בגוון אפור כהה מאוד/כמעט שחור.
    }

    public void draw(Canvas canvas, int p1EatenCount, int p2EatenCount) {
        // פונקציית הציור של קו האמצע. היא מקבלת את קנבס הציור ואת כמויות האבנים האכולות הנוכחיות של שני השחקנים מתוך מודל המשחק.

        canvas.drawRect(rect, paint);
        // ציור המלבן הגדול של קו האמצע החום המפריד בין שני חצאי הלוח.

        // חישוב רדיוס מדויק ותואם פרופורציונלית לאבנים האכולות על גבי הבר, כדי שייראו באותו הגודל כמו האבנים שעל המשולשים:
        float triangleSectionWidth = screenWidth / 13f;
        // חישוב רוחב סקציית משולש בסיסית.

        float triangleWidth = 0.9f * triangleSectionWidth;
        // חישוב רוחב הציור של המשולש (90% מגודל הסקציה).

        float checkerRadius = 0.42f * triangleWidth / 2f; // הקטנה קלה לשיפור המראה
        // קביעת רדיוס האבן האכולה כ-42% מחצי רוחב המשולש, ליצירת התאמה ויזואלית מושלמת לבר.

        for (int i = 0; i < p1EatenCount; i++) {
            // לולאת For הרצה כמספר האבנים האכולות שיש לשחקן 1 (הלבן):

            float cx = rect.centerX();
            // מיקום ה-X של מרכז האבן יהיה בדיוק במרכז ה-X של הבר (rect.centerX()).

            float cy = rect.top + checkerRadius + (i * 2.1f * checkerRadius) + 15;
            // חישוב מיקום ה-Y של האבן: הן מצוירות מלמעלה למטה. האבן הראשונה מתחילה בחלק העליון של המסך (`rect.top`), וכל אבן נוספת בלולאה (מוכפלת ב-i) נדחפת כלפי מטה מתחת לקודמתה עם מרווח קל (2.1), בתוספת היסט קטן של 15 פיקסלים מהקצה.

            canvas.drawCircle(cx, cy, checkerRadius, p1Paint);
            // ציור האבן האכולה הלבנה של שחקן 1 על גבי הבר.
        }

        for (int i = 0; i < p2EatenCount; i++) {
            // לולאת For הרצה כמספר האבנים האכולות שיש לשחקן 2 (הכהה):

            float cx = rect.centerX();
            // מיקום ה-X של מרכז האבן במרכז הבר.

            float cy = rect.bottom - checkerRadius - (i * 2.1f * checkerRadius) - 15;
            // חישוב מיקום ה-Y של האבן: שחקן 2 מצויר מלמטה למעלה! האבן הראשונה ממוקמת בתחתית המסך (`rect.bottom`), וכל אבן נוספת בלולאה נדחפת *כלפי מעלה* (סימן מינוס) מעל לקודמתה.

            canvas.drawCircle(cx, cy, checkerRadius, p2Paint);
            // ציור האבן האכולה הכהה של שחקן 2 על גבי הבר.
        }
    }

    public RectF getRect() { return rect; }
    // פונקציית גטר המחזירה את אובייקט מלבן גבולות הבר (משמש את ה-SurfaceView לחישוב מיקום הקוביות לידו).
}