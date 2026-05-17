package com.example.nevos_shesh_besh.shapes;
// שיוך לחבילת הצורות.

import android.graphics.Canvas;
import android.util.Log;
// ייבוא רכיב קנבס הציור ורכיב ה-Log להדפסת הודעות בדיקה במחשב.

public class CircleShape extends BaseShape {
// הגדרת מחלקת צורת העיגול, היורשת מ-BaseShape (מקבלת ממנה את ה-X, Y וה-Paint).

    protected float radius;
    // משתנה עשרוני פרטי/מוגן השומר את רדיוס העיגול (גודל העיגול מהמרכז לקצה).

    public boolean isActive = false;
    // משתנה בוליאני (אמת/שקר) המציין האם העיגול נמצא כרגע במצב פעיל או נבחר (למשל, אם השחקן לחץ על אבן זו והיא ממתינה למהלך).

    private static final String TAG = "CircleShape";
    // תג (Tag) קבוע מסוג מחרוזת, המשמש כמזהה עבור הודעות ה-Log בקונסולה של המפתח כדי שנדע שההודעות מגיעות ממחלקה זו.

    public CircleShape(float radius, int color, float x, float y) {
        // בנאי של צורת העיגול המקבל את הרדיוס, הצבע, ומיקומי ה-X וה-Y שלו.

        super(x, y, color);
        // פקודת super חובה הקוראת לבנאי של מחלקת האב (BaseShape) ומעבירה לו את המיקומים והצבע כדי שהוא יאתחל אותם ואת ה-Paint.

        this.radius = radius;
        // אתחול רדיוס העיגול הספציפי במשתנה המחלקה.
    }

    public float getY() { return y; }
    // פונקציית גטר המחזירה את מיקום ה-Y הנוכחי של העיגול.

    @Override
    public void draw(Canvas canvas) {
        // מימוש פונקציית הציור המופשטת של האב עבור עיגול.

        canvas.drawCircle(x, y, radius, paint);
        // פקודה מובנית של קנבס הציור באנדרואיד שמציירת פיזית עיגול מושלם על המסך לפי נקודת המרכז (x, y), רדיוס (radius), וצבע המברשת (paint).
    }

    @Override
    public boolean isTouched(float touchX, float touchY) {
        // מימוש פונקציית זיהוי נגיעת האצבע המופשטת של האב עבור עיגול.

        double dx = touchX - x;
        // חישוב המרחק על ציר ה-X בין נקודת הנגיעה של האצבע לבין נקודת מרכז העיגול שלנו.

        double dy = touchY - y;
        // חישוב המרחק על ציר ה-Y בין נקודת הנגיעה של האצבע לבין נקודת מרכז העיגול שלנו.

        // הדפסת הודעות לוג (Log.d) לצורכי דיבאג (ניקוי שגיאות) כדי שהמפתח יראה בזמן אמת בתוכנת ה-Android Studio את קואורדינטות הנגיעה והחישובים:
        Log.d(TAG, String.format("isTouched: touchX=%f, touchY=%f", touchX, touchY));
        Log.d(TAG, String.format("isTouched: x=%f, y=%f", x, y));
        Log.d(TAG, String.format("isTouched: x=%f, y=%f", dx, dy));

        return (dx * dx + dy * dy) <= (radius * radius);
        // טיפ חשוב מאוד למבחן: הקוד משתמש כאן במשפט פיתגורס מתמטי מדוייק כדי לדעת אם נגעו בעיגול ($A^2 + B^2 = C^2$). הוא מחשב את המרחק של הנגיעה בריבוע (dx*dx + dy*dy), ואם הוא קטן או שווה לרדיוס בריבוע (radius*radius), פירוש הדבר שהאצבע נחתה בתוך שטח הפנים של העיגול והפונקציה תחזיר true. אחרת, תחזיר false.
    }
}