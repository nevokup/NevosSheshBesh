package com.example.nevos_shesh_besh.shapes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.util.Log;
import com.example.nevos_shesh_besh.shapes.BaseShape;

public class TriangleShape extends BaseShape {
// מחלקה המייצגת משולש גרפי על הלוח, היורשת ממחלקת האב המופשטת BaseShape.

    private final float width;
    private final float height;
    // משתני הממדים של המשולש (רוחב הבסיס והגובה).

    private boolean isUpSideDown;
    // משתנה בוליאני הקובע האם המשולש פונה כלפי מעלה (בחלק התחתון של המסך) או הפוך כלפי מטה (בחלק העליון).

    private int circlesCount;
    // כמות החיילים (עיגולים) שצריך לצייר כרגע בתוך המשולש הזה.

    private final Path path;
    // אובייקט של מחלקת Path המשמש לחיבור נקודות גיאומטריות ליצירת צורת משולש סגורה לציור.

    private static final String TAG = "TriangleShape";

    public TriangleShape(float x, float y, float width, float height, boolean isUpSideDown, int color, int circlesCount) {
        // בנאי המאתחל את מיקום ה-X וה-Y, הממדים, כיוון המשולש, צבע המשולש וכמות החיילים הראשונית בו.
        super(x, y, color);
        this.width = width;
        this.height = height;
        this.path = new Path();
        this.isUpSideDown = isUpSideDown;
        this.circlesCount = circlesCount;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public boolean getIsUpSideDown() { return isUpSideDown; }

    @Override
    public void draw(Canvas canvas) {
        // פונקציית הציור המרכזית: היא בונה את שלוש נקודות המשולש לפי המיקום והכיוון, ממלאה אותו בצבע, ולאחר מכן מפעילה לולאה המציירת את חיילי המשחק (עיגולים) אחד מעל השני בתוך המשולש תוך זיהוי צבע השחקן (לבן או שחור) והאם החייל העליון מסומן כנבחר (אקטיבי).
        path.reset();
        if (!isUpSideDown) { // בניית משולש רגיל (פונה למעלה)
            path.moveTo(x - width / 2, y);
            path.lineTo(x, y - height);
            path.lineTo(x + width / 2, y);
            path.lineTo(x - width / 2, y);
        } else { // בניית משולש הפוך (פונה למטה)
            path.moveTo(x - width / 2, y);
            path.lineTo(x, y + height);
            path.lineTo(x + width / 2, y);
            path.lineTo(x - width / 2, y);
        }
        path.close();
        canvas.drawPath(path, paint); // ציור המשולש עצמו

        // שלב ציור החיילים בתוך המשולש במידה וקיימים
        if (circlesCount > 0) {
            int count = circlesCount;
            int color = Color.WHITE; // ברירת מחדל: שחקן 1 לבן
            int activeColor = Color.CYAN; // צבע זוהר לחייל שנבחר

            boolean drawActiveCircle = false;
            if (count >= 1000) { // ניתוח קוד: אם הערך מעל 1000 זה אומר שהחייל העליון נבחר כרגע לתנועה
                count -= 1000;
                drawActiveCircle = true;
            }

            if (count >= 100) { // ניתוח קוד: אם הערך מעל 100 החיילים שייכים לשחקן 2 (כהים)
                count -= 100;
                color = Color.rgb(40, 40, 40);
                activeColor = Color.YELLOW;
            }

            float drawRadius = (float) 0.45 * width / 2;
            float drawX = this.getX();
            float drawY;

            for (int i = 0; i < count; i++) { // לולאה המציירת את עיגולי החיילים זה על גבי זה (מחסנית חזותית)
                if (this.getIsUpSideDown()) {
                    drawY = this.getY() + drawRadius + 2 * drawRadius * i + 5;
                } else {
                    drawY = this.getY() - drawRadius - 2 * drawRadius * i - 5;
                }

                int finalColor = (drawActiveCircle && i == count - 1) ? activeColor : color;
                CircleShape circle = new CircleShape(drawRadius, finalColor, drawX, drawY);
                circle.draw(canvas);
            }
        }
    }

    public void draw(Canvas canvas, int circlesCount) {
        // פונקציה דרוסה (Overload) המאפשרת לעדכן את כמות החיילים המדויקת רגע לפני הציור בפועל.
        this.circlesCount = circlesCount;
        draw(canvas);
    }

    @Override
    public boolean isTouched(float touchX, float touchY) {
        // פונקציית זיהוי נגיעה (Hitbox): מקבלת את קואורדינטות הנגיעה של האצבע של המשתמש ומחזירה בוליאני המציין האם הלחיצה התרחשה בתוך השטח הגיאומטרי המוגדר של המשולש הזה.
        if (!(touchX >= x - width / 2 && touchX <= x + width / 2)) return false;
        if (isUpSideDown) {
            return touchY >= y && touchY <= y + height;
        } else {
            return touchY <= y && touchY >= y - height;
        }
    }
}