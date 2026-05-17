package com.example.nevos_shesh_besh.shapes;
// שיוך לחבילת הצורות (shapes) של הפרויקט.

import android.graphics.Canvas;
import android.graphics.Paint;
// ייבוא רכיבי הציור של אנדרואיד: קנבס (Canvas) ומברשת (Paint).

public abstract class BaseShape {
// הגדרת מחלקה מופשטת בשם BaseShape.

    protected float x, y;
    // משתני מיקום מסוג מספר עשרוני (float) המייצגים את קואורדינטות ה-X וה-Y של מרכז או נקודת ההתחלה של הצורה על גבי מסך המכשיר. הם מוגדרים כ-protected כדי שהמחלקות הילדות שיורשות ממנה יוכלו לגשת אליהם ישירות.

    protected Paint paint;
    // משתנה מסוג Paint המייצג את "מברשת הצבע" שבאמצעותה נקבע את הצבע, הסגנון והמאפיינים הויזואליים של הצורה בזמן הציור.

    public BaseShape(float x, float y, int color) {
        // בנאי של צורת הבסיס, המקבל מיקומי X ו-Y התחלתיים וקוד צבע של צביעה.

        this.x = x;
        this.y = y;
        // השמת המיקומים שנתקבלו בבנאי לתוך המשתנים של המחלקה.

        this.paint = new Paint();
        // יצירת מופע חדש של אובייקט מברשת הציור.

        this.paint.setColor(color);
        // הגדרת גוון הצבע של המברשת לפי הצבע שנתקבל בפרמטר.

        this.paint.setStyle(Paint.Style.FILL);
        // קביעת סגנון המברשת למצב FILL – צביעה מלאה ואטומה של כל שטח הפנים הפנימי של הצורה (ולא רק קווי מתאר).

        this.paint.setAntiAlias(true);
        // הפעלת תכונת Anti-Aliasing (החלקת קצוות). תכונה זו גורמת לקצוות של צורות עגולות או אלכסוניות להיראות חלקות ויפות על המסך ללא "פיקסלים שבורים".
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    // פונקציה ציבורית המאפשרת לשנות את המיקום של הצורה על המסך בכל עת (שימושי לצורך הזזת אבנים וכד').

    public abstract void draw(Canvas canvas);
    // הגדרת פונקציה מופשטת (abstract) בשם draw ללא גוף פנימי. טיפ למבחן: כל מחלקה שתירש מהמחלקה הזו (כמו עיגול או מלבן) *חייבת* לממש את הפונקציה הזו ולכתוב בתוכה את הלוגיקה הגרפית הספציפית שלה לציור על גבי קנבס הציור (canvas).

    public abstract boolean isTouched(float touchX, float touchY);
    // הגדרת פונקציה מופשטת נוספת שכל צורה חייבת לממש, שתפקידה לקבל את מיקום הלחיצה של אצבע המשתמש (touchX, touchY) ולהחזיר true אם נקודת הלחיצה נמצאת בתוך גבולות הצורה, או false אם היא מחוצה לה.
}