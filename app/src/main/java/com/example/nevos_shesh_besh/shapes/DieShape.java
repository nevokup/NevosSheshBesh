package com.example.nevos_shesh_besh.shapes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class DieShape {
// מחלקה המייצגת ומציירת קובייה גרפית על גבי הלוח.

    private int number;
    // הערך המספרי הנוכחי של הקובייה (1 עד 6).

    private float x, y, size;
    // המיקום הגיאומטרי (X, Y) של הקובייה על המסך והגודל (אורך ורוחב) שלה.

    private Paint paint;
    // מברשת המשמשת לקביעת המראה של ריבוע הקובייה (כמו צבע רקע).

    private Paint textPaint;
    // מברשת מיוחדת המשמשת לקביעת המראה של המספר המודפס בתוך הקובייה (גופן, צבע, גודל).

    public DieShape(int number, float x, float y, float size) {
        // בנאי המאתחל את ערך הקובייה, מיקומה, ויוצר את המברשות עם ההגדרות העיצוביות (רקע לבן, טקסט שחור ממורכז).
        this.number = number;
        this.x = x;
        this.y = y;
        this.size = size;

        this.paint = new Paint();
        this.paint.setColor(Color.WHITE);

        this.textPaint = new Paint();
        this.textPaint.setColor(Color.BLACK);
        this.textPaint.setTextSize(size / 2);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void draw(Canvas canvas) {
        // פונקציה המקבלת קנבס ומציירת עליו מלבן/ריבוע שמייצג את הקובייה, ולאחר מכן מחשבת את המרכז המדויק ומדפיסה עליו את הספרה הנוכחית.
        RectF rect = new RectF(x, y, x + size, y + size);
        canvas.drawRect(rect, paint);

        float textX = x + size / 2;
        float textY = y + size / 2 - ((textPaint.descent() + textPaint.ascent()) / 2); // חישוב מתמטי למרכוז אנכי מושלם של הטקסט
        canvas.drawText(String.valueOf(number), textX, textY, textPaint);
    }

    public void setNumber(int number) {
        // פונקציית עדכון (Setter) המאפשרת לשנות את ערך הקובייה לאחר הטלה חדשה.
        this.number = number;
    }
}