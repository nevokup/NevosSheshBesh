package com.example.nevos_shesh_besh.shapes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class MiddleLineShape {
    private RectF rect;
    private Paint paint;
    private Paint p1Paint;
    private Paint p2Paint;

    public MiddleLineShape(int screenWidth, int screenHeight, int numberOfTriangles) {
        float sectionWidth = (float) screenWidth / (numberOfTriangles + 1);
        float middleLineWidth = sectionWidth;
        float left = (screenWidth / 2f) - (middleLineWidth / 2f);
        float top = 0;
        float right = (screenWidth / 2f) + (middleLineWidth / 2f);
        float bottom = screenHeight;
        rect = new RectF(left, top, right, bottom);

        paint = new Paint();
        // צבע הבר - עץ כהה מאוד (כהה יותר מהרקע)
        paint.setColor(Color.rgb(60, 30, 10));

        p1Paint = new Paint();
        p1Paint.setColor(Color.WHITE); // שחקן 1 - לבן

        p2Paint = new Paint();
        p2Paint.setColor(Color.rgb(40, 40, 40)); // שחקן 2 - שחור/אפור כהה
    }

    public void draw(Canvas canvas, int p1EatenCount, int p2EatenCount) {
        canvas.drawRect(rect, paint);

        float checkerRadius = rect.width() * 0.45f;

        // ציור חיילים אכולים של שחקן 1 (למעלה)
        for (int i = 0; i < p1EatenCount; i++) {
            float cx = rect.centerX();
            float cy = rect.top + checkerRadius + (i * 2.2f * checkerRadius) + 10;
            canvas.drawCircle(cx, cy, checkerRadius, p1Paint);
        }

        // ציור חיילים אכולים של שחקן 2 (למטה)
        for (int i = 0; i < p2EatenCount; i++) {
            float cx = rect.centerX();
            float cy = rect.bottom - checkerRadius - (i * 2.2f * checkerRadius) - 10;
            canvas.drawCircle(cx, cy, checkerRadius, p2Paint);
        }
    }

    public RectF getRect() {
        return rect;
    }
}
