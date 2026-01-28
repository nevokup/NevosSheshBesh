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
        paint.setColor(Color.GREEN);

        p1Paint = new Paint();
        p1Paint.setColor(Color.BLUE);

        p2Paint = new Paint();
        p2Paint.setColor(Color.WHITE);
    }

    public void draw(Canvas canvas, int p1EatenCount, int p2EatenCount) {
        canvas.drawRect(rect, paint);

        float checkerRadius = rect.width() / 4;

        // Draw eaten P1 checkers
        for (int i = 0; i < p1EatenCount; i++) {
            float cx = rect.centerX();
            float cy = rect.bottom - checkerRadius - (i * 2 * checkerRadius);
            canvas.drawCircle(cx, cy, checkerRadius, p1Paint);
        }

        // Draw eaten P2 checkers
        for (int i = 0; i < p2EatenCount; i++) {
            float cx = rect.centerX();
            float cy = rect.top + checkerRadius + (i * 2 * checkerRadius);
            canvas.drawCircle(cx, cy, checkerRadius, p2Paint);
        }
    }

    public RectF getRect() {
        return rect;
    }
}
